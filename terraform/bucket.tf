resource "google_storage_bucket" "materials-bucket" {
  project       = var.project_id
  name          = "${var.app-name}-materials"
  location      = var.region
  force_destroy = false

  uniform_bucket_level_access = true
}