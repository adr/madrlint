///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 21+
//SOURCES app/src/main/java/neutra1/linter/Main.java
//SOURCES app/src/main/java/neutra1/linter/core/ASTTraverser.java
//SOURCES app/src/main/java/neutra1/linter/core/HtmlCommentRemoverExtension.java
//SOURCES app/src/main/java/neutra1/linter/core/Reporter.java
//SOURCES app/src/main/java/neutra1/linter/helper/IgnoreFileHandler.java
//SOURCES app/src/main/java/neutra1/linter/helper/LintContext.java
//SOURCES app/src/main/java/neutra1/linter/models/enums/DecisionOutcomeElements.java
//SOURCES app/src/main/java/neutra1/linter/models/enums/LinkType.java
//SOURCES app/src/main/java/neutra1/linter/models/enums/MandatorySection.java
//SOURCES app/src/main/java/neutra1/linter/models/enums/OptionalSection.java
//SOURCES app/src/main/java/neutra1/linter/models/enums/ResourceType.java
//SOURCES app/src/main/java/neutra1/linter/models/records/BulletListItemInfo.java
//SOURCES app/src/main/java/neutra1/linter/models/records/HeadingInfo.java
//SOURCES app/src/main/java/neutra1/linter/models/records/LinkInfo.java
//SOURCES app/src/main/java/neutra1/linter/models/records/MetadataInfo.java
//SOURCES app/src/main/java/neutra1/linter/models/records/ParagraphInfo.java
//SOURCES app/src/main/java/neutra1/linter/models/records/Violation.java
//SOURCES app/src/main/java/neutra1/linter/rules/AbstractRule.java
//SOURCES app/src/main/java/neutra1/linter/rules/HeadingRule.java
//SOURCES app/src/main/java/neutra1/linter/rules/IDirectoryRule.java
//SOURCES app/src/main/java/neutra1/linter/rules/IFileRule.java
//SOURCES app/src/main/java/neutra1/linter/rules/LinkRule.java
//SOURCES app/src/main/java/neutra1/linter/rules/MetadataRule.java
//SOURCES app/src/main/java/neutra1/linter/rules/NamingRule.java
//SOURCES app/src/main/java/neutra1/linter/rules/SectionRule.java
//SOURCES app/src/main/java/neutra1/linter/rules/impl/directory/Rule41.java
//SOURCES app/src/main/java/neutra1/linter/rules/impl/directory/Rule42.java
//SOURCES app/src/main/java/neutra1/linter/rules/impl/directory/Rule43.java
//SOURCES app/src/main/java/neutra1/linter/rules/impl/directory/Rule44.java
//SOURCES app/src/main/java/neutra1/linter/rules/impl/file/Rule01.java
//SOURCES app/src/main/java/neutra1/linter/rules/impl/file/Rule02.java
//SOURCES app/src/main/java/neutra1/linter/rules/impl/file/Rule03.java
//SOURCES app/src/main/java/neutra1/linter/rules/impl/file/Rule04.java
//SOURCES app/src/main/java/neutra1/linter/rules/impl/file/Rule05.java
//SOURCES app/src/main/java/neutra1/linter/rules/impl/file/Rule06.java
//SOURCES app/src/main/java/neutra1/linter/rules/impl/file/Rule07.java
//SOURCES app/src/main/java/neutra1/linter/rules/impl/file/Rule11.java
//SOURCES app/src/main/java/neutra1/linter/rules/impl/file/Rule12.java
//SOURCES app/src/main/java/neutra1/linter/rules/impl/file/Rule21.java
//SOURCES app/src/main/java/neutra1/linter/rules/impl/file/Rule31.java
//DEPS info.picocli:picocli:4.7.7
//DEPS org.projectlombok:lombok:1.18.38
//DEPS com.vladsch.flexmark:flexmark:0.64.8
//DEPS com.vladsch.flexmark:flexmark-ext-autolink:0.64.8
//DEPS com.vladsch.flexmark:flexmark-ext-yaml-front-matter:0.64.8
//DEPS com.github.sbaudoin:yamllint:1.6.1
//DEPS com.google.code.gson:gson:2.13.2
//COMPILE_OPTIONS -proc:full

public class madrlint {
    public static void main(String[] args) {
        neutra1.linter.Main.main(args);
    }
}
