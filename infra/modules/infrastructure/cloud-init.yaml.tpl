#cloud-config

package_update: true
package_upgrade: true

packages:
  - nginx
  - certbot
  - python3-certbot-nginx
  - ca-certificates
  - gnupg
  - lsb-release

write_files:
  - path: /etc/nginx/sites-available/medai
    permissions: '0644'
    content: |
      server {
          listen 80;
          server_name ${fqdn};

          client_max_body_size 50m;

          location /api/ {
              rewrite ^/api/(.*)$ /$1 break;
              proxy_pass http://127.0.0.1:8000;
              proxy_set_header Host $host;
              proxy_set_header X-Real-IP $remote_addr;
              proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
              proxy_set_header X-Forwarded-Proto $scheme;
          }

          location / {
              proxy_pass http://127.0.0.1:3000;
              proxy_set_header Host $host;
              proxy_set_header X-Real-IP $remote_addr;
              proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
              proxy_set_header X-Forwarded-Proto $scheme;
          }
      }
  - path: /etc/cron.d/acr-login
    permissions: '0644'
    content: |
      0 */2 * * * azureuser az acr login --name ${acr_name} --identity >/dev/null 2>&1

runcmd:
  - |
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null
    apt-get update -y
    apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
    systemctl enable --now docker
    usermod -aG docker azureuser
  - |
    curl -sL https://aka.ms/InstallAzureCLIDeb | bash
  - |
    rm -f /etc/nginx/sites-enabled/default
    ln -s /etc/nginx/sites-available/medai /etc/nginx/sites-enabled/medai
    nginx -t && systemctl restart nginx
  - |
    for i in $(seq 1 12); do
      su - azureuser -c "az login --identity --output none 2>/dev/null && az acr login --name ${acr_name} --identity" && break
      sleep 10
    done
  - |
    sleep 30
    certbot --nginx -d ${fqdn} --non-interactive --agree-tos --redirect -m ${certbot_email} || echo "Certbot failed — retry manually"
