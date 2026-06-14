resource "aws_cloudwatch_log_group" "app" {
  name              = "/ecs/${local.name_prefix}"
  retention_in_days = var.cloudwatch_log_retention_days
}
