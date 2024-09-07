locals {
  labels = {
    "app-name" = var.app-name
  }
}

variable "project_id" {
  type        = string
  description = "ID Google project"
}

variable "region" {
  type        = string
  description = "Region Google project"
}

variable "app-name" {
  type        = string
  description = "Name of application to use as resource prefix"
}