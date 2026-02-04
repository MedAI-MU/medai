# 🔁 GitHub Workflow

## 🌿 Branching Strategy

We use **3 main branches**:

* `dev` 🧪 – Development branch

  > All PRs should be merged **into** this branch.

* `staging` 🧷 – Pre-release branch

  > Used for **final testing** before release.

* `main` 🚀 – Production branch

  > Only contains the **stable release** of the app.

---

## 🚧 Working on a New Feature

1. 🔀 **Create a feature branch**
   Example: `feature/user-endpoint`

2. 📤 **Open a Pull Request** comparing your branch to `dev`

3. ✅ If **GitHub Actions tests pass**, a reviewer can approve and merge the PR

   * ⚠️ Use **squash merge** to keep the commit history clean.

4. 🛡️ **Cybersecurity team** reviews merged PRs and flags any issues

   * 🔧 Once fixed, changes are merged into `staging`.

5. 🧪 **Final tests** are done on `staging`

6. 🚀 If all is good, changes are merged into `main`.

---

## 🚫 Do NOT Push Directly To

* `dev`
* `staging`
* `main`

Use **feature branches + pull requests** instead.

---

## ❓ Why Use Pull Requests?

* 👥 We're a team of **11 developers across 5 tracks**
* 🧠 Reduces **merge conflicts**
* 🔍 Easier to **review and track** changes
* 🧪 Fellow track members can **review your code**
* 🐞 Reviewers might catch bugs you missed
* 📚 Encourages **shared learning**

---

## 🧪 Each Track Must Include Tests

* Add **at least unit tests** in your app
* 🧾 Run tests with one command instead of testing manually
* ❓ Why not just test your new feature?

  * Because it might **break other features** in your app

> 🚨 “App” here refers to **your track’s codebase**, not the entire project.

## Backend Instructions for Mobile and Frontend

* Run `make run` to start the backend server.
* Visit `http://localhost:8000/api/docs` to access the API documentation.
