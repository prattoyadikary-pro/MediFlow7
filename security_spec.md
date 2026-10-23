# Security Specification: MediFlow Firestore Rules

## 1. Data Invariants
1. **Strict User Ownership**: Every `/users/{userId}` document belongs exclusively to the authenticated user whose `request.auth.uid == userId`, and `data.userId == request.auth.uid`.
2. **PII Isolation**: Personal health & contact details (`email`, `phone`, `age`, `bloodGroup`, `weightKg`, `emergencyContact`) can only be read or written by the document owner (`request.auth.uid == userId`).
3. **Immutable Metadata**: `userId` and `createdAt` are immutable after document creation.
4. **Temporal Integrity**: `createdAt` and `updatedAt` must be server-generated `timestamp` values (`<= request.time`).
5. **Bounded Strings**: Every string field enforces strict `.size()` bounds to prevent Denial-of-Wallet payload attacks.

## 2. The "Dirty Dozen" Payloads
1. **Unauthenticated Read**: `unauthDb.collection("users").doc("alice").get()` -> Reject
2. **Cross-User PII Read**: `bobDb.collection("users").doc("alice").get()` -> Reject
3. **Identity Spoofing on Create**: `aliceDb.collection("users").doc("alice").set({ userId: "bob", ... })` -> Reject
4. **Path Mismatch on Create**: `aliceDb.collection("users").doc("bob").set({ userId: "alice", ... })` -> Reject
5. **Shadow / Ghost Field Injection**: `aliceDb.collection("users").doc("alice").set({ ..., isAdmin: true })` -> Reject
6. **Missing Required Field (`displayName`)**: `aliceDb.collection("users").doc("alice").set({ userId: "alice", email: "a@b.com" })` -> Reject
7. **Oversized String Attack**: `displayName` with 500 chars (> 100 limit) -> Reject
8. **Type Poisoning**: `age: 28` (int instead of string) -> Reject
9. **Immutable `createdAt` Tampering on Update**: Modifying `createdAt` during update -> Reject
10. **Immutable `userId` Tampering on Update**: Modifying `userId` during update -> Reject
11. **Unfiltered Collection List Query**: `aliceDb.collection("users").get()` without `.where("userId", "==", "alice")` -> Reject
12. **Cross-User Delete**: `bobDb.collection("users").doc("alice").delete()` -> Reject
