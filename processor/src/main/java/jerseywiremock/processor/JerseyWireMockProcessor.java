package jerseywiremock.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import jerseywiremock.annotations.JerseyWireMock;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

@AutoService(Processor.class)
public class JerseyWireMockProcessor extends AbstractProcessor {
    private Filer filer;
    private boolean alreadyMadeFile = false;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<String>();
        annotations.add(JerseyWireMock.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (alreadyMadeFile) {
            return true;
        }

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(WireMockRule.class, "wireMockRule")
                .addParameter(ObjectMapper.class, "objectMapper")
                .build();

        TypeSpec fooMocker = TypeSpec.classBuilder("FooMocker")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(constructor)
                .build();

        JavaFile javaFile = JavaFile.builder("example", fooMocker)
                .build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        alreadyMadeFile = true;
        return true;
    }
}
