# ADR-LOCAL-001 — project-local shared errors and standard Java quality

Version: 1.0.0. Date: 2026-10-30. Status: **owner-authorised for this project; implemented locally, not merged/released**.

## Authority and scope

Prithvi confirmed he is authorised to adopt project-owned error/quality components, said error types should follow the existing design, and requested normal Java Checkstyle. This applies only to feature 001-api-layer-http-https in the personal GitHub Omega-API-Layer repository. It does not claim approval for another Omega deployment or alter AA.1.1.1/other module requirements.

The earlier assumption of an already published external error artifact and an available corporate Checkstyle/PMD artifact was false. This amendment changes their **delivery/source**, not error safety, hierarchy, localisation, naming, coverage, nonblocking requirements, review gates or security controls.

## Decision

1. Provide a real reactor-local **gr.eurobank.omega:omega-errors-lib:0.1.0-SNAPSHOT** module, package **com.eurobank.omega.error.contract**. All active application modules declare it in compile scope; it has no production Spring dependency. It is not a fake alias for an externally published platform release.
2. Implement the hierarchy from error-handling-rules.md §3: OmegaException, OlTechnicalException, OlBusinessDataException, BusinessErrorException, DataValidationException, TransportException, ServiceUnavailableException and TimeoutException. Recoverable exceptions are checked per org-constitution/constitution.md. A final immutable ErrorContext validates metadata before the subclassable exception hierarchy is constructed, avoiding partial-construction/finalizer risks without deprecated finalizers or SpotBugs suppressions. Retry eligibility comes from the subtree, not messages/status inspection.
3. Implement the 24 active baseline constants from contracts/error-catalogue.md: 12 existing gateway codes and 12 active non-gateway codes. Do not activate the four starred gateway additions pending OD-05 or parked Admin codes. Schema 400/422 and priority 429/503 variants remain source-constrained, not arbitrary overrides.
4. Provide fixed en-GB/el-GR title/detail/resolution bundles and immutable header negotiation. Newly authored English/Greek wording is **complete and tested but pending human terminology/translation review**; code completeness is not that review. No rejected payload value is stored in field-error records. Internal causes are not client bodies.
5. Use unmodified **Google Java Style Checkstyle** from the pinned Checkstyle distribution, centrally configured in the parent POM. This is the chosen conventional Java style; it is not a bespoke external corporate ruleset. A single shared path-and-specific-message compatibility exception permits only otherwise CamelCase *IT class names under src/test/java, because naming-standards §1.3 and Maven require that suffix. No production class, other abbreviation, method or rule is exempted; it does not suppress an Omega naming requirement.
6. Use the **bundled default Maven PMD Java rules**, with their Apache licence header retained in the shared root ruleset, plus the existing per-method maximum-complexity requirement. No default rule is suppressed. Every active module inherits the same checks, including test sources. Checkstyle warnings fail the build.
7. Checks run in normal verify and remain enabled with foundation/container profiles. Existing SpotBugs, JaCoCo, Java/vendor/Maven enforcement and deployment-disabled state remain. An organisation-quality profile name is retained only as a compatibility alias, not a gate requiring absent files or an exemption.

## Not decided here

HTTP Problem representation conflict (B03), proposed new gateway codes, tenant/metadata hash/mapping/endpoint semantics, full gateway subclasses, service HTTP handlers, genuine Tasks-PR/profile/authority evidence, complete G1 and runtime Mongo/container cleanup are not silently resolved.

Google Style alone does not enforce every Omega-specific wire/API/architecture convention. Those standards remain binding and must be checked by the applicable contract/static/architecture tests and review; this ADR does not assert complete compliance with them.

## Ownership and compatibility

Prithvi owns the local shared module, parent quality configuration and future shared API changes. Shiksha consumes the pinned baseline and must not fork it. Original task IDs/checkboxes and parked scope remain unchanged. This is the authorised local delivery for T012/T022/T023-related foundations, not a claim those whole original tasks or their reviews are complete.

There is no push, commit, PR, merge, artifact publication or change to the Azure workspace/Boards flow from this ADR. Preserve mandatory review/approval gates. Future migration to a genuine platform release must compare types/codes/locales and be versioned/reviewed.
