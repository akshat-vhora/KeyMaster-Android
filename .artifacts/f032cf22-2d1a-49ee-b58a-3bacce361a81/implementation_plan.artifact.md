# Fix Token Expiration and Credentials Storing

This plan addresses two issues: the lack of redirection on token expiration and the failure to store/auto-fill remembered credentials.

## User Review Required

> [!IMPORTANT]
> The redirection logic relies on catching 401 Unauthorized errors from the server. If the server returns a different code for expiration, we may need to adjust.

## Proposed Changes

### [Component] PanelViewModel (Navigation & Error Handling)

#### [MODIFY] [PanelRoot.kt](file:///C:/Users/vhora/MyWork/Panel/PanelApp/app/src/main/java/com/akshatmodz/panel/ui/app/PanelRoot.kt)
*   **Fix `loadWorkspace`**: Remove the internal `try-catch` blocks from the `async` calls. Let errors propagate so `restore()` or `runTask` can catch them.
*   **Fix `restore`**: Ensure it handles 401 errors specifically to clear the session and show the login screen.
*   **Fix `runTask`**: Centralize 401 handling to trigger `signOut()` and show an appropriate message.
*   **Cleanup**: Remove the duplicate `runBulk` function that was accidentally added.
*   **Propagate Errors**: Update `reloadKeys`, `reloadProfile`, and `refresh` to either use `runTask` or propagate `ApiException`.

### [Component] SessionManager (Credentials Storing)

#### [MODIFY] [SessionManager.kt](file:///C:/Users/vhora/MyWork/Panel/PanelApp/app/src/main/java/com/akshatmodz/panel/data/SessionManager.kt)
*   Add logging or fallback for `EncryptedSharedPreferences` initialization. (Optional, if it's a platform issue).
*   Ensure `apply()` is called and check if `token` is being saved correctly in `DataStore`.

#### [MODIFY] [PanelRoot.kt](file:///C:/Users/vhora/MyWork/Panel/PanelApp/app/src/main/java/com/akshatmodz/panel/ui/app/PanelRoot.kt)
*   Check if `rememberMe` is correctly read from the state during `submitAuth`.

## Verification Plan

### Manual Verification
1.  **Token Expiration**:
    *   Manually invalidate the token in DataStore or wait for it to expire.
    *   Perform any action (like refreshing keys).
    *   The app should clear the session and redirect to the Login screen with a "Session expired" message.
2.  **Remember Credentials**:
    *   Open the app, check "Remember device", and log in.
    *   Sign out.
    *   Verify that the Email and Password fields are auto-filled on the login screen.
    *   Restart the app and verify the same.
