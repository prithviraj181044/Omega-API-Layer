# Java quality baseline attribution

The PMD baseline in pmd-ruleset.xml is copied from the Apache Maven PMD Plugin 3.28.0 bundled rulesets/java/maven-pmd-plugin-default.xml, with its Apache License header retained. One standard PMD CyclomaticComplexity rule enforces the existing project limit; none of the default rules is removed or suppressed.

Checkstyle loads the unmodified google_checks.xml bundled in the pinned Checkstyle distribution. All active modules inherit the same parent configuration. Google Java Format is a local formatting tool, not a production dependency or a substitute for the checks.

This is an owner-authorised project-local baseline. It is not a claim that Google Java Style alone checks every Omega API/semantic/security convention; those requirements remain binding and need their own applicable tests/review.
