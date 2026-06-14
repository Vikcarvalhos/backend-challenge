output "aws_region" {
  description = "AWS region configured for this stack."
  value       = var.aws_region
}

output "name_prefix" {
  description = "Prefix used to name AWS resources."
  value       = local.name_prefix
}
