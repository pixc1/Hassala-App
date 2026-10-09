# Hassala (حصّالة) - Custom Circle Management, PDF Reporting & JSON Import/Export

An evolution of the Hassala Fintech application providing complete user control: clean-slate start with no default members, custom circle configuration (name, contribution, duration, currency), member addition and deletion, JSON circle sharing and importing, native PDF report generation, and dedicated Cairo Arabic typography paired with the dark iOS 26 Liquid Glass aesthetic.

***

## User Review & Critical Decisions

> [!IMPORTANT]
> The following product capabilities define this upgrade based on your requirements:

- **Clean Slate Onboarding**: On first launch, the app starts with no pre-filled members. Users can create a brand new circle from scratch or import an existing circle file.
- **Full Custom Circle Configuration**: Users can customize Circle Name, Monthly Contribution, Duration (number of months/members), Currency Symbol/Code (e.g. SAR ر.س, AED د.إ, USD $, EGP ج.م, QAR ر.ق, KWD د.ك), and Starting Month.
- **Member Management (Add & Remove)**: Full control to add real members (name, phone, contribution, turn order) and delete/remove members at any time with automatic recalculation of total pool funds.
- **Share & Import via JSON**:
  - Export the active circle as a structured `.json` file and share it via Android Share Sheet or WhatsApp.
  - Import a shared `.json` circle file directly into the app using the Android file picker or direct JSON input.
- **Native PDF Summary Report Export**: Generates a professional multi-section PDF document using Android's native `android.graphics.pdf.PdfDocument`:
  - Header: Circle title, unique escrow ID, creation timestamp, and currency.
  - Financial Summary: Total pool goal, monthly contribution, collected amount, and remaining balance.
  - Full Member Table: Turn order, member name, phone number, payout month, and payment status badge (Paid / Pending).
  - Shareable immediately via Android Share Sheet (WhatsApp, Mail, Drive, Print).
- **Cairo Font Typography**: Exclusively styled with the classic, elegant Google Font **Cairo** across all Arabic titles, body text, buttons, and badges.

***

## 1. Overview & Core Concept

- **What It Does**: Transforms Hassala into a personal ROSCA manager where users create their own circles from scratch, manage peer members, export formal PDF reports for transparency among participants, and exchange circle data with peers via portable JSON files.
- **Target Audience**: Organizers of community money circles (*Jam'eya*) who need a trustworthy, clean tool to track payments, share PDF audit sheets with group members on WhatsApp, and exchange circle configurations.
- **Key Value**: 100% customizable without forced demo data, transparent accounting with PDF exports, and portable backups via JSON.

***

## 2. User Experience & Visual Design

### Key User Flows

1. **Clean Slate & Create Circle Flow**:
   - When no circle exists or when creating a new one: An elegant Liquid Glass Welcome Card with "Create New Circle" and "Import Circle (JSON)" buttons.
   - Creation Modal: Input Circle Name, Monthly Dues ($ or SAR), Duration (e.g. 10 months), Currency picker (ر.س, د.إ, $, ج.م, etc.), and Start Month.
2. **Member Management (Add & Remove)**:
   - "Add Member" sheet: Enter Name, WhatsApp Phone, Turn #, and Contribution.
   - In Members list: Swipe or tap "Delete Member" icon with confirmation dialog to remove members.
   - Dynamic recalculation: The progress ring, total pool, and remaining balances update instantaneously in Room SQLite.
3. **JSON Share & Import Flow**:
   - Top bar / Circle actions menu has "Share Circle (JSON)" button.
   - Generates a formatted JSON file (`hassala_circle_<id>.json`) and triggers Android Share Sheet.
   - "Import JSON" allows picking a file or pasting JSON to instantly restore or load a shared circle.
4. **PDF Report Export Flow**:
   - Dedicated "Export PDF Summary" action in the Dashboard and Members screens.
   - Renders a branded PDF report with financial headers, summary boxes, and member status tables.
   - Automatically opens Android Share Sheet so the user can send the PDF directly to their WhatsApp group or save to Google Drive.

### Visual Identity
- **Design System**: Dark Liquid Glass iOS 26 (Obsidian `#030706` background with floating emerald & gold ambient aurora mesh).
- **Typography**: Exclusively powered by **Cairo** (`R.font.cairo`) with weights ranging from Medium to ExtraBold for high readability.
- **Components**: Translucent frosted acrylic cards, glowing neon status chips, specular bevel highlights, and floating action dock.

***

## 3. Technical Architecture & Data Strategy

```
┌─────────────────────────────────────────────────────────────┐
│                       Hassala App Engine                    │
│                                                             │
│   ┌─────────────────────────────────────────────────────┐   │
│   │                 Navigation & Top Bar                │   │
│   │   [Logo]  [Share JSON]  [Export PDF]  [AR/EN]       │   │
│   └─────────────────────────────────────────────────────┘   │
│                              │                              │
│   ┌─────────────────────────────────────────────────────┐   │
│   │                    Active Views                     │   │
│   │  ├─ Zero-State: Welcome Card (Create / Import JSON) │   │
│   │  ├─ Dashboard: Progress Ring & Financial Metrics    │   │
│   │  ├─ Members: Roster with Add/Delete & WhatsApp      │   │
│   │  └─ Timeline: Chronological Turn Schedule           │   │
│   └─────────────────────────────────────────────────────┘   │
│                              │                              │
│   ┌─────────────────────────────────────────────────────┐   │
│   │             Import / Export Services                │   │
│   │  ├─ JsonCircleExporter: Serialize / Deserialize     │   │
│   │  └─ PdfReportGenerator: android.graphics.pdf        │   │
│   └─────────────────────────────────────────────────────┘   │
│                              │                              │
│   ┌─────────────────────────────────────────────────────┐   │
│   │               Room Database Persistence             │   │
│   │  ├─ CircleDao & MemberDao (Full CRUD: Insert/Delete)│   │
│   │  └─ PaymentReceiptDao                               │   │
│   └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### Key Technical Implementations
1. **JSON Serialization / Deserialization**:
   - `JsonCircleExporter`: Serializes `MoneyCircle` and `List<CircleMember>` into portable JSON and writes to application cache, sharing via `FileProvider` or text intent.
2. **Native Android PDF Generation**:
   - `PdfReportGenerator`: Uses Android SDK's `android.graphics.pdf.PdfDocument` with a customized `Paint` and `Canvas` layout (A4 standard), drawing headers, financial summary boxes, and styled member status tables.
3. **Clean-Slate Database Seeding**:
   - Database starts clean with no hardcoded members. User can create their own custom circle immediately.
