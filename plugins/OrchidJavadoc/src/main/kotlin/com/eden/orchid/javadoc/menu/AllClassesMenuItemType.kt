package com.eden.orchid.javadoc.menu

import com.eden.orchid.api.OrchidContext
import com.eden.orchid.api.options.annotations.Description
import com.eden.orchid.api.options.annotations.Option
import com.eden.orchid.api.options.annotations.StringDefault
import com.eden.orchid.api.theme.menus.menuItem.OrchidMenuItem
import com.eden.orchid.api.theme.menus.menuItem.OrchidMenuItemImpl
import com.eden.orchid.api.theme.pages.OrchidPage
import com.eden.orchid.javadoc.models.JavadocModel
import java.util.ArrayList
import javax.inject.Inject

@Description("All Javadoc class pages.", name = "Javadoc Classes")
class AllClassesMenuItemType
@Inject
constructor(
        context: OrchidContext,
        private val model: JavadocModel
) : OrchidMenuItem(context, "javadocClasses", 100) {

    @Option
    @StringDefault("All Classes")
    lateinit var title: String

    override fun getMenuItems(): List<OrchidMenuItemImpl> {
        val items = ArrayList<OrchidMenuItemImpl>()
        val pages = ArrayList<OrchidPage>(model.allClasses)
        pages.sortBy { it.title }
        items.add(OrchidMenuItemImpl.Builder(context)
                .title(title)
                .pages(pages)
                .build()
        )
        return items
    }

}
