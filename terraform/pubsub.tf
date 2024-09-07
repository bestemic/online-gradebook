resource "google_pubsub_topic" "notification-topic" {
  name    = "${var.app-name}-notification"
  project = var.project_id
  labels  = local.labels
}

resource "google_pubsub_subscription" "notification-subscription" {
  name                 = "${var.app-name}-notification-scr"
  topic                = google_pubsub_topic.notification-topic.id
  labels               = local.labels
  ack_deadline_seconds = 20
}