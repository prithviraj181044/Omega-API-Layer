# Omega API Layer application context

This checkout is the user-authorised GitHub application repo for feature 001-api-layer-http-https. The PDLC workspace is the enclosing Omega_API_LAYER workspace; from this nested checkout it is ../../.. . Read that workspace's AGENTS.md, selected effective constitution, .specify/rules, feature sources and task-distribution owner work orders before feature changes. If those sources are unavailable in a standalone clone, stop and obtain the matching versioned context; do not invent replacements.

Base Java package: com.eurobank.omega.api. Mandatory layer names: contract, api, service, domain, adapter, config. Domain code is framework-free. Reactive WebFlux/MongoDB; no blocking or business logic in gateway handlers. Shared error contracts use the owner-authorised reactor-local omega-errors-lib per docs/architecture/ADR-LOCAL-001-errors-and-quality.md; do not fork it or invent raw codes. Other project approvals/standards remain in force.

Local runtime: Podman. Database product: MongoDB. Do not pull unapproved test/runtime images or disable cleanup without a recorded runtime decision. Bind addresses, sensitivity, localisation, correlation and security rules still apply.

Prithvi owns root/module POMs, omega-errors-lib, api-contract, api-policy-core, api-platform, api-gateway and shared tooling. Shiksha owns the allocated validation/metadata and event services except POMs. Admin/backend/Swagger/Portal/UI and Atlas/Cortex TCP are parked. Follow the exact selected task/file policy; do not overwrite peer or unrelated changes.

This is a local incomplete foundation, not G1 approval. No push, PR, merge or task-state update without authorisation and real evidence. Worker mode produces reports only. There are no production service bootstraps or endpoints yet.
