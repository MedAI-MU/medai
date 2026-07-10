module "infrastructure" {
  source = "../../modules/infrastructure"

  environment                      = "prod"
  location                         = var.location
  vm_size                          = var.vm_size
  acr_name                         = var.acr_name
  existing_acr_resource_group_name = var.existing_acr_resource_group_name
  domain_name_label                = var.domain_name_label
  custom_domain                    = var.custom_domain

  ssh_public_key    = var.ssh_public_key
  certbot_email     = var.certbot_email
  pg_admin_password = var.pg_admin_password
  client_ip_address = var.client_ip_address
}
