package learning.basics.archTest;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "learning.basics")
public class LayeredArchitectureTest {

    // 1. Controller dürfen nicht direkt auf Repositories zugreifen (müssen über Services gehen)
    @ArchTest
    static final ArchRule controller_should_not_access_repository_directly =
        noClasses().that().resideInAPackage("..controller..")
            .should().dependOnClassesThat().resideInAPackage("..repository..");

    // 2. Models dürfen keine Controller oder DTOs kennen
    @ArchTest
    static final ArchRule model_should_not_depend_on_web_layer =
        noClasses().that().resideInAPackage("..model..")
            .should().dependOnClassesThat().resideInAnyPackage("..controller..", "..dto..");

    // 3. Services dürfen keine UI/Controller-Klassen importieren
    @ArchTest
    static final ArchRule service_should_not_depend_on_controller =
        noClasses().that().resideInAPackage("..service..")
            .should().dependOnClassesThat().resideInAPackage("..controller..");
}