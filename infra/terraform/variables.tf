variable "aws_region" {
  description = "AWS region where the infrastructure will be provisioned."
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Project name used to prefix AWS resources."
  type        = string
  default     = "jwt-validator"
}

variable "environment" {
  description = "Deployment environment name."
  type        = string
  default     = "dev"
}

variable "owner" {
  description = "Owner tag value used for cost allocation and resource identification."
  type        = string
  default     = "backend-challenge"
}
