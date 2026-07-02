output "vm_fqdn" {
  value       = module.infrastructure.vm_fqdn
  description = "Full FQDN of the VM public IP"
}

output "vm_public_ip" {
  value       = module.infrastructure.vm_public_ip
  description = "Public IP address of the VM"
}

output "acr_login_server" {
  value       = module.infrastructure.acr_login_server
  description = "Login server of the Azure Container Registry"
}
