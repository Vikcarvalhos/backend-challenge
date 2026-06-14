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

variable "cloudwatch_log_retention_days" {
  description = "Number of days to retain application logs in CloudWatch."
  type        = number
  default     = 7
}

variable "ecr_images_to_keep" {
  description = "Maximum number of recent ECR images to keep."
  type        = number
  default     = 5
}

variable "vpc_cidr" {
  description = "CIDR block used by the application VPC."
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "CIDR blocks used by public subnets."
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "container_port" {
  description = "Port exposed by the application container."
  type        = number
  default     = 8080
}
