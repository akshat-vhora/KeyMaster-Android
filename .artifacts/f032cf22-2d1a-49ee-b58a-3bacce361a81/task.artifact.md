# List Performance Fix Tasks

- `[x]` Optimize `KeyCard` for smooth scrolling
    - `[x]` Replace `IntrinsicSize.Min` stripe with `drawBehind` logic
    - `[x]` Simplify status badge and metadata icons (remove `Surface`)
    - `[x]` Flatten footer and action bar layout
- `[x]` Move Key Actions to Bottom Sheet
    - `[x]` Add `selectedKeyForActions` to state
    - `[x]` Implement `KeyActionsBottomSheetContent`
    - `[x]` Update `KeyCard` to be minimal and trigger bottom sheet
- `[x]` Verify build and smooth scrolling
