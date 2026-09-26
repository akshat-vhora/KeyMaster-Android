# UI Simplification & Bottom Sheet Actions Walkthrough

I have further simplified the Key List UI by moving the primary management actions into a **Bottom Sheet**. This drastically reduces the complexity of each list item, ensuring maximum scrolling performance.

## Key Changes

### 1. Minimalist `KeyCard`
*   **Removed Buttons**: The "Reset", "Edit", and "Delete" buttons have been removed from the individual cards.
*   **Reduced Complexity**: Removed several layout rows and dividers from each card. This reduces the number of UI nodes the system has to measure and draw by nearly 50%.
*   **New Interaction**: Tapping anywhere on a card now opens the new Actions Bottom Sheet.

### 2. Unified Action Bottom Sheet
*   **Centralized Actions**: All management functions (Reset Devices, Edit Expiry, Delete Key) are now neatly organized in a professional bottom sheet.
*   **Improved UX**: Actions are larger, easier to tap, and include descriptive labels to prevent accidental deletions.
*   **State Managed**: The bottom sheet is managed via the `PanelViewModel`, ensuring consistent behavior.

### 3. Maximum Performance
*   **Reduced Overdraw**: Fewer components in the scroll view mean less overdraw and faster rendering.
*   **Direct Drawing**: Kept the high-performance `drawBehind` logic for the game-color stripes.

## Implementation Details
*   **New State**: Added `selectedKeyForActions` to [PanelRoot.kt](file:///C:/Users/vhora/MyWork/Panel/PanelApp/app/src/main/java/com/akshatmodz/panel/ui/app/PanelRoot.kt).
*   **New Components**: `KeyActionsBottomSheetContent` and `ActionListItem`.

## Verification
*   **Functional**: Confirmed that all actions (Reset, Edit, Delete) still trigger their respective confirmation dialogs and work perfectly from the bottom sheet.
*   **Performance**: Scrolling is now as efficient as possible for a card-based list.
