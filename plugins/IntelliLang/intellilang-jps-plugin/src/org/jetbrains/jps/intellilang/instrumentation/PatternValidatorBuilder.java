// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.jps.intellilang.instrumentation;

import com.intellij.compiler.instrumentation.InstrumentationClassFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.incremental.BinaryContent;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.CompiledClass;
import org.jetbrains.jps.incremental.instrumentation.BaseInstrumentingBuilder;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import org.jetbrains.jps.intellilang.model.JpsIntelliLangConfiguration;
import org.jetbrains.jps.intellilang.model.JpsIntelliLangExtensionService;
import org.jetbrains.jps.model.JpsGlobal;
import org.jetbrains.org.objectweb.asm.ClassReader;
import org.jetbrains.org.objectweb.asm.ClassWriter;

/**
 * @author Eugene Zhuravlev
 */
public class PatternValidatorBuilder extends BaseInstrumentingBuilder{
  public PatternValidatorBuilder() { }

  @NotNull
  @Override
  public String getPresentableName() {
    return "IntelliLang Pattern Validator";
  }

  @Override
  protected boolean isEnabled(CompileContext context, ModuleChunk chunk) {
    JpsGlobal project = context.getProjectDescriptor().getModel().getGlobal();
    JpsIntelliLangConfiguration config = JpsIntelliLangExtensionService.getInstance().getConfiguration(project);
    return config.getInstrumentationType() != InstrumentationType.NONE;
  }

  @Override
  protected boolean canInstrument(CompiledClass compiledClass, int classFileVersion) {
    return !"module-info".equals(compiledClass.getClassName());
  }

  @Nullable
  @Override
  protected BinaryContent instrument(CompileContext context,
                                     CompiledClass compiled,
                                     ClassReader reader,
                                     ClassWriter writer,
                                     InstrumentationClassFinder finder) {
    JpsGlobal project = context.getProjectDescriptor().getModel().getGlobal();
    JpsIntelliLangConfiguration config = JpsIntelliLangExtensionService.getInstance().getConfiguration(project);
    PatternInstrumenter instrumenter = new PatternInstrumenter(config.getPatternAnnotationClass(), writer, config.getInstrumentationType(), finder);
    try {
      reader.accept(instrumenter, 0);
      if (instrumenter.instrumented()) {
        return new BinaryContent(writer.toByteArray());
      }
    }
    catch (InstrumentationException e) {
      context.processMessage(new CompilerMessage(getPresentableName(), BuildMessage.Kind.ERROR, e.getMessage()));
    }
    return null;
  }

  @Override
  protected String getProgressMessage() {
    return "Adding pattern assertions...";
  }
}