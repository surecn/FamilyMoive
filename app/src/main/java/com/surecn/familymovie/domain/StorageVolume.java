/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.surecn.familymovie.domain;

import android.content.Context;

/**
 * Description of a storage volume and its capabilities, including the
 * filesystem path where it may be mounted.
 */
public class StorageVolume {

    // TODO: switch to more durable token
    private int mStorageId;

    private final String mPath;
    private final String mDescription;
    private final boolean mPrimary;
    private final boolean mRemovable;
    private final boolean mEmulated;
    private final int mMtpReserveSpace;
    private final boolean mAllowMassStorage;
    /** Maximum file size for the storage, or zero for no limit */
    private final long mMaxFileSize;

    private String mUuid;
    private String mUserLabel;
    private String mState;

    // StorageVolume extra for ACTION_MEDIA_REMOVED, ACTION_MEDIA_UNMOUNTED, ACTION_MEDIA_CHECKING,
    // ACTION_MEDIA_NOFS, ACTION_MEDIA_MOUNTED, ACTION_MEDIA_SHARED, ACTION_MEDIA_UNSHARED,
    // ACTION_MEDIA_BAD_REMOVAL, ACTION_MEDIA_UNMOUNTABLE and ACTION_MEDIA_EJECT broadcasts.
    public static final String EXTRA_STORAGE_VOLUME = "storage_volume";

    public StorageVolume(String path, String description, boolean primary, boolean removable,
                         boolean emulated, int mtpReserveSpace, boolean allowMassStorage, long maxFileSize) {
        mPath = path;
        mDescription = description;
        mPrimary = primary;
        mRemovable = removable;
        mEmulated = emulated;
        mMtpReserveSpace = mtpReserveSpace;
        mAllowMassStorage = allowMassStorage;
        mMaxFileSize = maxFileSize;
    }

    /**
     * Returns the mount path for the volume.
     *
     * @return the mount path
     */
    public String getPath() {
        return mPath.toString();
    }

    public String getPathFile() {
        return mPath;
    }

    /**
     * Returns a user visible description of the volume.
     *
     * @return the volume description
     */
    public String getDescription(Context context) {
        return mDescription;
    }

    public boolean isPrimary() {
        return mPrimary;
    }

    /**
     * Returns true if the volume is removable.
     *
     * @return is removable
     */
    public boolean isRemovable() {
        return mRemovable;
    }

    /**
     * Returns true if the volume is emulated.
     *
     * @return is removable
     */
    public boolean isEmulated() {
        return mEmulated;
    }

    /**
     * Returns the MTP storage ID for the volume.
     * this is also used for the storage_id column in the media provider.
     *
     * @return MTP storage ID
     */
    public int getStorageId() {
        return mStorageId;
    }

    /**
     * Do not call this unless you are MountService
     */
    public void setStorageId(int index) {
        // storage ID is 0x00010001 for primary storage,
        // then 0x00020001, 0x00030001, etc. for secondary storages
        mStorageId = ((index + 1) << 16) + 1;
    }

    /**
     * Number of megabytes of space to leave unallocated by MTP.
     * MTP will subtract this value from the free space it reports back
     * to the host via GetStorageInfo, and will not allow new files to
     * be added via MTP if there is less than this amount left free in the storage.
     * If MTP has dedicated storage this value should be zero, but if MTP is
     * sharing storage with the rest of the system, set this to a positive value
     * to ensure that MTP activity does not result in the storage being
     * too close to full.
     *
     * @return MTP reserve space
     */
    public int getMtpReserveSpace() {
        return mMtpReserveSpace;
    }

    /**
     * Returns true if this volume can be shared via USB mass storage.
     *
     * @return whether mass storage is allowed
     */
    public boolean allowMassStorage() {
        return mAllowMassStorage;
    }

    /**
     * Returns maximum file size for the volume, or zero if it is unbounded.
     *
     * @return maximum file size
     */
    public long getMaxFileSize() {
        return mMaxFileSize;
    }

    public void setUuid(String uuid) {
        mUuid = uuid;
    }

    public String getUuid() {
        return mUuid;
    }

    /**
     * Parse and return volume UUID as FAT volume ID, or return -1 if unable to
     * parse or UUID is unknown.
     */
    public int getFatVolumeId() {
        if (mUuid == null || mUuid.length() != 9) {
            return -1;
        }
        try {
            return (int)Long.parseLong(mUuid.replace("-", ""), 16);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void setUserLabel(String userLabel) {
        mUserLabel = userLabel;
    }

    public String getUserLabel() {
        return mUserLabel;
    }

    public void setState(String state) {
        mState = state;
    }

    public String getState() {
        return mState;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StorageVolume && mPath != null) {
            StorageVolume volume = (StorageVolume)obj;
            return (mPath.equals(volume.mPath));
        }
        return false;
    }
}
