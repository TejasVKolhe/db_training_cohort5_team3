# db_training

## API Versioning

- All public API endpoints are exposed under `/api/v1`.
- Breaking API changes must be introduced under a new version (for example, `/api/v2`).
- Deprecated endpoints remain available until their sunset date and return deprecation headers pointing clients to the successor version.