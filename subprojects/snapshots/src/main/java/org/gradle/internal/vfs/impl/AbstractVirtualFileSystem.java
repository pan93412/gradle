/*
 * Copyright 2020 the original author or authors.
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

package org.gradle.internal.vfs.impl;

import org.gradle.internal.snapshot.CompleteFileSystemLocationSnapshot;
import org.gradle.internal.snapshot.MetadataSnapshot;
import org.gradle.internal.snapshot.SnapshotHierarchy;
import org.gradle.internal.vfs.VirtualFileSystem;

import java.util.Optional;

public abstract class AbstractVirtualFileSystem implements VirtualFileSystem {

    @Override
    public Optional<CompleteFileSystemLocationSnapshot> getSnapshot(String absolutePath) {
        return getRoot().getSnapshot(absolutePath);
    }

    @Override
    public Optional<MetadataSnapshot> getMetadata(String absolutePath) {
        return getRoot().getMetadata(absolutePath);
    }

    @Override
    public void store(String absolutePath, MetadataSnapshot snapshot) {
        update((root, diffListener) -> root.store(absolutePath, snapshot, diffListener));
    }

    @Override
    public void invalidate(String absolutePath) {
        update((root, diffListener) -> root.invalidate(absolutePath, diffListener));
    }

    @Override
    public void invalidateAll() {
        update((root, diffListener) -> {
            root.visitSnapshotRoots(diffListener::nodeRemoved);
            return root.empty();
        });
    }

    protected abstract void update(UpdateFunction updateFunction);

    protected abstract SnapshotHierarchy getRoot();

    /**
     * Updates the snapshot hierarchy, passing a {@link SnapshotHierarchy.NodeDiffListener} to the calls on {@link SnapshotHierarchy}.
     */
    public interface UpdateFunction {
        SnapshotHierarchy update(SnapshotHierarchy root, SnapshotHierarchy.NodeDiffListener diffListener);
    }
}
