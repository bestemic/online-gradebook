resource "google_sql_database_instance" "database-instance" {
  project          = var.project_id
  name             = "${var.app-name}-database-instance"
  region           = var.region
  database_version = "MYSQL_8_0"
}