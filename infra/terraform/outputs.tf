output "aws_region" {
  description = "AWS region configured for this stack."
  value       = var.aws_region
}

output "name_prefix" {
  description = "Prefix used to name AWS resources."
  value       = local.name_prefix
}

output "ecr_repository_name" {
  description = "Name of the ECR repository used by the application."
  value       = aws_ecr_repository.app.name
}

output "ecr_repository_url" {
  description = "URL of the ECR repository used by the application."
  value       = aws_ecr_repository.app.repository_url
}

output "cloudwatch_log_group_name" {
  description = "Name of the CloudWatch Log Group used by ECS tasks."
  value       = aws_cloudwatch_log_group.app.name
}
