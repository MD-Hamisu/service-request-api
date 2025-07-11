# Summary of Changes to Service Request System

This document summarizes the changes made to the Service Request System to implement user synchronization from an external Staff Profile System, add HOD tracking to the `Department` entity, restrict `updateRequestStatus` to HODs.

## 1. User Synchronization Implementation
- **Objective**: Replace manual user registration with synchronization from the external Staff Profile System.
- **Files Modified**:
    - **AuthController.java**:
        - Removed `/signup` endpoint to disable manual registration.
        - Added `/api/auth/synchronize` endpoint to trigger asynchronous user synchronization.
    - **UserServiceImpl.java**:
        - Added `synchronizeUsers` method to fetch user data from `URLConstants.GET_USERS` (mapped to `/api/external/staffs`).
        - Assigns random temporary passwords for new users and sends reset emails via `EmailService`.
        - Ensures one HOD per department by removing `HOD` role from existing HODs when a new HOD is assigned.
    - **ExternalApiController.java** (Staff Profile System):
        - Updated `mapStaffData` in `/api/external/staffs` to return fields compatible with `synchronizeUsers`

## 2. Department Entity Update for HOD Tracking
- **Objective**: Add a `HODUser` field to the `Department` entity to explicitly track the HOD.
- **Files Modified**:
    - **Department.java**:
        - Added `@OneToOne` relationship with `User` for `HODUser`, mapped to `hod_user_id` column in `departments` table.
      - **UserServiceImpl.java**:
          - `synchronizeUsers` sets `Department.HODUser` when a user with the `HOD` role is synchronized.
          - Clears `HODUser` from the department if the existing HOD is replaced.
          - Saves department changes.

## 3. Restrict `updateRequestStatus` to HOD
- **Objective**: Ensure only the HOD of the `ServiceRequest`’s `targetDepartment` can update its status.
- **Files Modified**:
    - **ServiceRequestServiceImpl.java**:
        - Added authorization check in `updateRequestStatus` using `SecurityUtil.getCurrentUser()`.
        - Verifies if the current user matches `request.getTargetDepartment().getHODUser()`.
        - Throws `HttpStatus.FORBIDDEN` if the user is not the HOD or if no HOD is assigned.
        - Preserved existing logic for status updates, rejection reason validation, email notifications, and response creation.

    