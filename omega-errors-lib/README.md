# Project-owned Omega errors library

Coordinates: `gr.eurobank.omega:omega-errors-lib:0.1.0-SNAPSHOT`. Package: `com.eurobank.omega.error.contract`.

This is the real owner-authorised reactor-local implementation described in ../docs/architecture/ADR-LOCAL-001-errors-and-quality.md, not a placeholder or a claim of an external platform release. All active modules declare it at compile scope. Prithvi owns its API and catalogue changes.

## Included

- 24 active baseline constants: 12 existing gateway + 12 non-Admin conditions from the source catalogue. Four starred gateway proposals and parked Admin conditions are not activated.
- Checked OmegaException hierarchy, technical/business retry classification by exception type, and a validated immutable ErrorContext that preserves correlation IDs and constrains status variants.
- Immutable ValidationFailure metadata with no rejected-value member.
- Complete fixed en-GB/el-GR title/detail/resolution resources and explicit language negotiation. Construct ErrorMessages at application bootstrap to load resources before reactive request handling.

Newly authored wording, particularly Greek terminology, still needs human review. Tests prove completeness/behaviour, not linguistic or release approval. Never serialize an exception/cause directly to a client; the WebFlux Problem mapper remains separate work and the source representation conflict is not silently decided here. Gateway-specific concrete wrappers remain in their planned task scope.

## Build

From the application root with Temurin 25:

~~~powershell
.\mvnw.cmd -pl omega-errors-lib -am verify
~~~

The normal parent quality gates apply. A local install is permitted for consumer builds; no artifact has been published remotely and full G1/feature approval has not been declared.
