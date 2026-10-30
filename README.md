# Omega API Layer — local shared foundation

Local application target: personal GitHub Omega-API-Layer. Runtime: Podman. Database product: MongoDB, confirmed by Prithvi. Changes remain on local setup/prithvi-foundation, uncommitted/unpushed unless a later explicit action says otherwise.

**This is not a merged G1 or production-ready application.** No HTTP endpoint, metadata extraction/repository or gateway policy is implemented. Admin/Portal/UI and TCP remain parked. Empty service JARs do not prove feature coverage.

## Normal build

Use Temurin 25 and the pinned Maven wrapper. From this application directory:

~~~powershell
.\mvnw.cmd verify
~~~

The normal build runs standard Google Java Checkstyle (including tests and warning failures), the shared Maven-default PMD Java baseline plus the existing complexity requirement, SpotBugs, tests, toolchain checks and JaCoCo. No unavailable external ruleset property is required. The one documented compatibility filter permits the mandatory CamelCase IT suffix in test sources; other naming violations still fail. foundation-smoke and organisation-quality are compatibility profile names, not ways to disable quality checks.

## Shared errors

The real reactor-local omega-errors-lib module supplies typed errors, checked exceptions and fixed English/Greek resources. See [the module](omega-errors-lib/README.md) and [the owner-authorised decision](docs/architecture/ADR-LOCAL-001-errors-and-quality.md). This replaces the nonexistent external-artifact assumption for this project only. Human wording/Greek review and proposed codes/HTTP mapper decisions are not falsely marked complete.

## Podman and MongoDB

The container-smoke profile adds the tagged API reachability IT; it does not disable Java quality checks. Configure the discovered compatible endpoint with consent on each developer's machine. API connectivity is not a successful Mongo replica-set/transaction/tenant or cleanup test. The earlier Ryuk callback failure remains recorded in the PDLC setup evidence; cleanup was not disabled.

MongoConfig wires a reactive transaction manager without credentials. TenantScopedRepository and real approved-image Mongo integration tests remain unfinished. The test factory requires an explicitly approved image and does not start a default container. No production service bootstrap or allow-all security stub is provided.

## Still required

Actual reviewed/merged G1, genuine workflow/effective-constitution/Tasks-PR evidence, affected design/contract/data-classification decisions, tenant repository/context and metadata mapping/hash/type semantics, runtime Mongo/cleanup tests and security/release qualification. Tool version existence/static-analysis success is not a CVSS or licence/security sign-off. Deployment remains disabled in this foundation.
