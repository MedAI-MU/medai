# 🎓 MedAI Graduation Project

## 🌟 Vision

* 🧠 AI medical app that accepts medical scans and generates reports.
* 🩺 Users can book appointments with doctors:

  * 📋 Follows hospital workflow for appointments.
  * 📅 Patient selects from available time slots.
  * 🔔 Assistant receives notification for new appointments.
  * ⏳ Appointment remains pending until approved or rejected by manager/assistant.

    * ✅ Manager can **accept** or ❌ **cancel** the appointment.
  * 🚫 Blacklist feature for problematic users.
* 📤 Automatically sends reports to doctors.
* 📷 Doctors can upload scans via website (file upload) or mobile app (camera).
* 🗂️ Patient medical history is maintained.
* 💬 Doctors can input and send diagnoses (optionally) to patients.
* 💊 Should app suggest medications based on reports/diagnosis?
* ⚠️ Should app warn about possible serious or alternative diseases?
* ⚡ Reports delivered to doctors **instantly** (real-time).
* 👥 Role-based views: separate dashboards for patients, doctors, and managers.
* 🔗 Integration with external apps (e.g., WhatsApp):

  * 📲 Notifications from the app can also be sent through WhatsApp.

---

## 🧰 Tech Stack

* 💻 **Frontend**: Next.js
* 🐍 **Backend**: Python Django
* 📱 **Mobile**: Native (Android/iOS)
* 🔌 **API Structure**: REST

  * 📸 Handles mostly image data — REST preferred over GraphQL.
* 🐳 **Dockerization**:

  * Backend: Dockerized both locally and in production.
  * Frontend: Dockerized in production (local is optional).

---

## ⚙️ GitHub Actions (CI/CD)

* 🔁 Every track should implement **tests** in GitHub CI/CD for pull requests.
* ✅ PRs should **only be merged** if all tests pass.
* 🧪 I can assist with test setup for your track.

---

## 🗂️ Main Workflow

* 📆 **Weekly Reports**:

  * Each **track leader** posts a report in the board channel every week:

    * What was done ✅
    * What’s planned for next week 📌
* 🛡️ **Security**:

  * Open GitHub issues for vulnerabilities found.
  * Use proper labels (e.g., `backend`, `frontend`).
  * Summarize open issues in weekly reports and notify affected tracks.

---

### 🔄 Track Collaboration

* 👥 Work proceeds concurrently by **tracks**.

  * AI team works independently (no blockers).
  * Security team waits if no new changes exist.
  * Backend can **delay AI integration** until finalized.
  * Frontend/mobile should flag any **backend dependency blockers** in board channel.
* ⚠️ To reduce blockers:

  * Keep PRs small and merge them quickly.
  * Optimize the review and merge timeframe.

---

## 🗓️ Monthly Board Meeting

* 📣 At the end of each month:

  * Hold a **board meeting** to wrap up and plan the next month.
  * 🧑‍🤝‍🧑 Other team members can join optionally.
