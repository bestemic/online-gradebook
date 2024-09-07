terraform {
  backend "gcs" {
    bucket = "gh-actions-terraform-state"
    prefix = "terraform/state/prod"
  }
}