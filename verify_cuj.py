from playwright.sync_api import sync_playwright

def run_cuj(page):
    page.goto("http://localhost:3000/auth/login")
    page.wait_for_timeout(1000)

    # Note: If the backend is empty, we need to create a user first.
    # Let's try filling by placeholder to see if it finds it.
    try:
        page.get_by_placeholder("john@example.com").fill("abdelrahman.ghozal10@gmail.com")
    except Exception as e:
        print("Could not fill email:", e)
        page.screenshot(path="/home/jules/verification/screenshots/error.png")
        raise e

    page.wait_for_timeout(500)
    page.get_by_placeholder("••••••••").fill("password")
    page.wait_for_timeout(500)
    page.get_by_role("button", name="Log In").click()
    page.wait_for_timeout(1500)

    # Note: We will probably hit the real dashboard here. Let's see if the page renders.
    # Take screenshot at the dashboard
    page.screenshot(path="/home/jules/verification/screenshots/verification.png")
    page.wait_for_timeout(1000)

if __name__ == "__main__":
    import os
    os.makedirs("/home/jules/verification/videos", exist_ok=True)
    os.makedirs("/home/jules/verification/screenshots", exist_ok=True)
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        context = browser.new_context(
            record_video_dir="/home/jules/verification/videos"
        )
        page = context.new_page()
        try:
            run_cuj(page)
        finally:
            context.close()
            browser.close()
