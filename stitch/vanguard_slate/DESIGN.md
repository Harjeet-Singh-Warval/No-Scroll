# Design System Document: The Kinetic Sanctuary

## 1. Overview & Creative North Star
This design system is built for the high-achieving Indian male (18–28), a demographic that values efficiency, modern aesthetics, and digital discipline. We are moving away from the "utility-first" look of standard productivity tools toward a **"Kinetic Sanctuary"**—an editorial-inspired environment that feels as calm as a meditation space but as high-performance as a luxury sports car.

By utilizing intentional asymmetry, expansive negative space, and a refined tonal hierarchy, we create an experience that reduces cognitive load. We aren't just building an app; we are curating a focus-state.

---

## 2. Colors & Surface Philosophy
The palette is rooted in deep, midnight neutrals paired with an electric, high-energy primary accent. 

### The "No-Line" Rule
To achieve a premium editorial feel, designers are strictly prohibited from using solid 1px borders for sectioning or layout containment. Boundaries must be defined through **Background Color Shifts**. 
*   **Example:** A `surface-container-low` card sitting on a `surface` background provides all the separation needed. Let the pixels breathe.

### Surface Hierarchy & Nesting
Treat the UI as a physical stack of semi-matte materials. 
*   **Base:** `surface` (#131318)
*   **Sub-sections:** `surface-container-low` (#1b1b20)
*   **Primary Cards:** `surface-container` (#1f1f25)
*   **Interactive/Elevated Elements:** `surface-container-highest` (#35343a)
*   **The Depth Principle:** When nesting an element (like a stat pill inside a card), the inner element should always use a higher container tier than its parent to "lift" toward the user.

### The "Glass & Gradient" Rule
To prevent the UI from feeling flat or "template-like," use Glassmorphism for floating overlays (e.g., Bottom Navigation or Floating Action Buttons). 
*   **Specs:** `surface` color at 70% opacity + 20px Backdrop Blur.
*   **Gradients:** Use a subtle linear gradient on primary CTAs (`primary` to `primary_container`) to add visual "soul" and a sense of forward motion.

---

## 3. Typography
We use **Plus Jakarta Sans** as our sole typeface. It offers a geometric clarity that feels contemporary and authoritative.

*   **Display Scale (`display-lg`, `display-md`):** Reserved for focus timers and primary daily stats. These should be bold, tracked slightly tighter (-2%), and feel like a piece of art rather than a label.
*   **Headline & Title:** Use for page headers and section titles. Apply high contrast between `headline-lg` and `body-md` to create a clear editorial hierarchy.
*   **Body & Labels:** Use `body-md` for general content. Use `label-md` in uppercase with +5% letter spacing for secondary metadata (e.g., "TASK DURATION").

---

## 4. Elevation & Depth
Traditional drop shadows are too heavy for this aesthetic. We achieve hierarchy through **Tonal Layering**.

*   **Ambient Shadows:** If a "floating" effect is necessary for a global action, use an extra-diffused shadow: `X: 0, Y: 12, Blur: 40, Spread: 0`, with the color set to `on_surface` at 6% opacity. This mimics natural light rather than a digital drop shadow.
*   **The "Ghost Border":** Per the user request for subtle borders, we will only use a "Ghost Border" fallback for accessibility. Use the `outline_variant` token at **15% opacity**. This creates a hint of a edge without cluttering the visual field.
*   **Roundedness:** We lean into the "Hyper-Rounded" aesthetic. 
    *   **Cards/Containers:** `xl` (3rem or 20dp as requested).
    *   **Buttons/Pills:** `full` (9999px) to create a soft, friendly interaction point.

---

## 5. Components

### Cards & Lists
*   **Prohibition:** Never use divider lines. 
*   **Implementation:** Separate list items using the spacing scale (e.g., `spacing-3`). To distinguish sections, transition from a `surface` background to a `surface-container-low` background.

### Buttons
*   **Primary:** `primary` background, `on_primary` text. Uses `full` roundedness. 
*   **Secondary:** `surface-container-highest` background with a "Ghost Border" (15% `outline_variant`).
*   **States:** On press, the button should scale down to 96% to provide tactile feedback without needing a shadow change.

### The "Focus Pulse" (Custom Component)
For the focus timer, use a `display-lg` stat in the center of the screen, surrounded by a `primary` ring with a 10% opacity "glow" using a large blur. This becomes the heartbeat of the app.

### Input Fields
*   **Style:** No borders. Use `surface-container-lowest` as the fill. 
*   **Interaction:** When focused, the background shifts to `surface-container-high` and a subtle 1px `primary` Ghost Border appears at 40% opacity.

---

## 6. Do’s and Don’ts

### Do:
*   **Use Asymmetry:** Place a large `display-sm` stat on the left and a small `label-md` description on the right to create a professional, editorial layout.
*   **Leverage White Space:** If a screen feels crowded, double the spacing values (e.g., move from `spacing-4` to `spacing-8`).
*   **Tint Your Greys:** Use `on_surface_variant` for secondary text to ensure the "dark" mode feels deep and intentional, not just black and white.

### Don't:
*   **Don’t use 100% Opaque Lines:** It breaks the "Kinetic Sanctuary" feel.
*   **Don’t use standard Material Blue:** Only use the `primary` (#c4c0ff / #6C63FF) for accents.
*   **Don’t use Sharp Corners:** Nothing in this system should be less than `sm` (0.5rem) roundedness. Sharp corners represent stress; we represent focus.