resource "google_sql_database_instance" "database-instance" {
  project          = var.project_id
  name             = "${var.app-name}-database-instance"
  region           = var.region
  database_version = "MYSQL_8_0"

  settings {
    tier        = "db-f1-micro"
    user_labels = local.labels
  }
}

resource "google_sql_database" "database" {
  name     = "gradebook"
  instance = google_sql_database_instance.database-instance.name
}

resource "google_sql_user" "user" {
  name     = "gradebook_user"
  instance = google_sql_database_instance.database-instance.name
  password = var.db_password
}