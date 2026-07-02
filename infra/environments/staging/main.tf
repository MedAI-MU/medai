module "infrastructure" {
  source = "../../modules/infrastructure"

  environment       = "staging"
  location          = var.location
  vm_size           = var.vm_size
  acr_sku           = var.acr_sku
  acr_name          = var.acr_name
  create_acr        = true
  domain_name_label = var.domain_name_label

  ssh_public_key = var.ssh_public_key
  certbot_email  = var.certbot_email
}
