/*
 * Taken out of an unpublished Adventure API Addition,
 * once it gets added to the Adventure API, we'll remove this,
 * until then thanks to syldium for developing this
 * original source: https://github.com/syldium/adventure/blob/feature/split/api/src/main/java/net/kyori/adventure/text/ComponentSplitting.java
 */
package de.stckoverflw.stckutils.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.Style
import java.util.Deque
import java.util.LinkedList
import java.util.regex.Pattern

internal object ComponentSplitting {
    fun split(self: Component, regex: Pattern): List<Component> {
        return ArrayList(split0(self, Style.empty(), regex))
    }

    private fun split0(self: Component, parentStyle: Style, regex: Pattern): Deque<Component> {
        val parts: Deque<Component> = LinkedList()
        val style = self.style().merge(parentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET)
        if (self is TextComponent) {
            val content = self.content()
            val result = regex.split(content, -1)
            parts.addLast(Component.text(result[0], self.style()))
            for (i in 1 until result.size) {
                // Create new component with the computed style for each string part
                parts.add(Component.text(result[i], style))
            }
        } else {
            // Remove the children to split them recursively
            parts.addLast(self.children(emptyList()))
        }
        var sibling = false
        for (child in self.children()) {
            val result = split0(child, style, regex)
            // Append the first part to the last parent component
            // and remove empty components
            val root = parts.pollLast()
            val first = result.pollFirst()
            if (isEmpty(first)) {
                parts.addLast(root)
            } else if (isEmpty(root)) {
                val newStyle = first.style().merge(root.style(), Style.Merge.Strategy.IF_ABSENT_ON_TARGET)
                parts.addLast(first.style(newStyle))
                sibling = true
            } else if (sibling) {
                parts.addLast(Component.empty().children(listOf(root, first)))
                sibling = false
            } else {
                parts.addLast(root.append(first))
            }

            // Add the remaining parts
            if (parts.addAll(result)) {
                sibling = true
            }
        }
        return parts
    }

    /**
     * Tests if the component has no content nor children.
     *
     * @param component the component to test
     * @return `true` if empty
     */
    private fun isEmpty(component: Component): Boolean {
        return (
            component is TextComponent &&
                component.content().isEmpty() &&
                component.children().isEmpty()
            )
    }
}
