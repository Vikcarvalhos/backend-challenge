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

output "vpc_id" {
  description = "ID of the application VPC."
  value       = aws_vpc.main.id
}

output "public_subnet_ids" {
  description = "IDs of public subnets used by the load balancer and ECS service."
  value       = aws_subnet.public[*].id
}

output "alb_security_group_id" {
  description = "ID of the Application Load Balancer security group."
  value       = aws_security_group.alb.id
}

output "ecs_service_security_group_id" {
  description = "ID of the ECS service security group."
  value       = aws_security_group.ecs_service.id
}
