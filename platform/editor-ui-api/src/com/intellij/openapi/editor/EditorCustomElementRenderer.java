/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.openapi.editor;

import com.intellij.openapi.editor.markup.TextAttributes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * An interface, defining size and representation of custom visual element in editor.
 *
 * @see InlayModel#addInlineElement(int, boolean, EditorCustomElementRenderer)
 * @see InlayModel#addBlockElement(int, boolean, boolean, int, EditorCustomElementRenderer)
 * @see Inlay#getRenderer()
 *
 * @since 2016.3
 */
public interface EditorCustomElementRenderer {
  /**
   * Defines width of custom element (in pixels)
   *
   * @since 2018.3
   */
  default int calcWidthInPixels(@NotNull Inlay inlay) {
    return calcWidthInPixels(inlay.getEditor());
  }

  /**
   * @deprecated Override/use {@link #calcWidthInPixels(Inlay)} instead. This method will be removed.
   */
  default int calcWidthInPixels(@NotNull Editor editor) {
    return 0;
  }

  /**
   * Defines height of custom element (in pixels). This value is currently not used for 'inline' elements.
   *
   * @since 2018.3
   */
  default int calcHeightInPixels(@NotNull Inlay inlay) {
    return inlay.getEditor().getLineHeight();
  }

  /**
   * Implements painting for the custom region.
   * 
   * @param targetRegion region where painting should be performed
   * @param textAttributes attributes of surrounding text
   *
   * @since 2018.3
   */
  default void paint(@NotNull Inlay inlay, @NotNull Graphics g, @NotNull Rectangle targetRegion, @NotNull TextAttributes textAttributes) {
    paint(inlay.getEditor(), g, targetRegion, textAttributes);
  }

  /**
   * @deprecated Override/use {@link #paint(Inlay, Graphics, Rectangle, TextAttributes)} instead. This method will be removed.
   */
  default void paint(@NotNull Editor editor, @NotNull Graphics g, @NotNull Rectangle targetRegion, @NotNull TextAttributes textAttributes) {
  }

  /**
   * Returns a registered id of action group, which is to be used for displaying context menu for the given custom element.
   * If {@code null} is returned, standard editor's context menu will be displayed upon corresponding mouse event.
   *
   * @since 2018.3
   */
  @Nullable
  default String getContextMenuGroupId(@NotNull Inlay inlay) {
    //noinspection deprecation
    return getContextMenuGroupId();
  }

  /**
   * @deprecated Override/use {@link #getContextMenuGroupId(Inlay)} instead. This method will be removed.
   */
  default String getContextMenuGroupId() {
    return null;
  }
}
