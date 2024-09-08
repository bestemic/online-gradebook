resource "google_artifact_registry_repository" "repository" {
  location      = var.region
  project       = var.project_id
  repository_id = "${var.app-name}-repository"
  format        = "DOCKER"
  labels        = local.labels
}