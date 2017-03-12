package com.eden.orchid.server;

import com.caseyjbrooks.clog.Clog;
import com.eden.orchid.Orchid;
import com.eden.orchid.api.OrchidContext;
import com.eden.orchid.api.events.On;
import com.eden.orchid.api.tasks.OrchidTask;
import com.eden.orchid.server.server.StaticServer;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.EventListener;

@Singleton
public class ServeTask extends OrchidTask implements EventListener {

    private OrchidContext context;
    private StaticServer server;
    private FileWatcher watcher;

    @Inject
    public ServeTask(OrchidContext context, StaticServer server, FileWatcher watcher) {
        this.context = context;
        this.server = server;
        this.watcher = watcher;
    }

    @Override
    public String getName() {
        return "serve";
    }

    @Override
    public String getDescription() {
        return "Makes it easier to create content for your Orchid site by watching your resources for changes and " +
                "rebuilding the site on any changes. A static HTTP server is also created in the root of your site and " +
                "the baseUrl set to this server's address so you can preview the output.";
    }

    @Override
    public void run() {
        if (context.query("options.resourcesDir") != null) {
            String rootDir = context.query("options.resourcesDir").toString();

            File file = new File(rootDir);

            if (file.exists() && file.isDirectory()) {
                Clog.i("Watching root resources directory for changes");

                JSONObject rootJson = context.getRoot();
                JSONObject optionsJson = rootJson.getJSONObject("options");

                context.build();
                server.start(8080);

                optionsJson.put("baseUrl", "http://localhost:" + server.getPort());

                watcher.startWatching(rootDir);
            }
            else {
                Clog.w("OrchidResourcesImpl directory doesn't exist or isn't is a directory");
            }
        }
        else {
            Clog.w("There is no resources directory to watch");
        }
    }

    @On(Orchid.Events.FILES_CHANGED)
    public void onFilesChanges() {
        context.build();
    }

    @On(Orchid.Events.SHUTDOWN)
    public void onCleanShutdown() {
        new Thread(server::stop);
    }
}
