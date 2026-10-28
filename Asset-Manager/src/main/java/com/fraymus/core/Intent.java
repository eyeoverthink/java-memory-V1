package com.fraymus.core;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Intent - Represents a high-level goal or subtask
 * Author: Vaughn Scott
 */
public class Intent {
    private final String id;
    private final String operation;
    private final String scope;
    private final boolean destructive;
    private final boolean privileged;
    private final boolean networked;
    private final boolean unrestricted;
    private final boolean humanApproved;
    private final Map<String, String> attributes;

    public Intent(String id) {
        this(id, id, "", false, false, false, false, false, Map.of());
    }

    public Intent(
        String id,
        String operation,
        String scope,
        boolean destructive,
        boolean privileged,
        boolean networked,
        boolean unrestricted,
        boolean humanApproved,
        Map<String, String> attributes
    ) {
        this.id = id;
        this.operation = operation == null || operation.isBlank() ? id : operation;
        this.scope = scope == null ? "" : scope;
        this.destructive = destructive;
        this.privileged = privileged;
        this.networked = networked;
        this.unrestricted = unrestricted;
        this.humanApproved = humanApproved;
        this.attributes = Map.copyOf(attributes == null ? Map.of() : new LinkedHashMap<>(attributes));
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public String getId() {
        return id;
    }

    public String getOperation() {
        return operation;
    }

    public String getScope() {
        return scope;
    }

    public boolean isDestructive() {
        return destructive;
    }

    public boolean isPrivileged() {
        return privileged;
    }

    public boolean isNetworked() {
        return networked;
    }

    public boolean isUnrestricted() {
        return unrestricted;
    }

    public boolean isHumanApproved() {
        return humanApproved;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public boolean hasBoundedScope() {
        if (scope.isBlank()) {
            return false;
        }
        String normalized = scope.toLowerCase();
        return !normalized.contains("*")
            && !normalized.contains("..")
            && !"any".equals(normalized)
            && !"unbounded".equals(normalized)
            && !"global".equals(normalized);
    }

    @Override
    public String toString() {
        return "Intent[id=" + id
            + ", operation=" + operation
            + ", scope=" + scope
            + ", destructive=" + destructive
            + ", privileged=" + privileged
            + ", networked=" + networked
            + ", unrestricted=" + unrestricted
            + ", humanApproved=" + humanApproved
            + "]";
    }

    public static final class Builder {
        private final String id;
        private String operation = "";
        private String scope = "";
        private boolean destructive;
        private boolean privileged;
        private boolean networked;
        private boolean unrestricted;
        private boolean humanApproved;
        private final Map<String, String> attributes = new LinkedHashMap<>();

        private Builder(String id) {
            this.id = id;
        }

        public Builder operation(String operation) {
            this.operation = operation;
            return this;
        }

        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public Builder destructive(boolean destructive) {
            this.destructive = destructive;
            return this;
        }

        public Builder privileged(boolean privileged) {
            this.privileged = privileged;
            return this;
        }

        public Builder networked(boolean networked) {
            this.networked = networked;
            return this;
        }

        public Builder unrestricted(boolean unrestricted) {
            this.unrestricted = unrestricted;
            return this;
        }

        public Builder humanApproved(boolean humanApproved) {
            this.humanApproved = humanApproved;
            return this;
        }

        public Builder attribute(String key, String value) {
            if (key != null && !key.isBlank() && value != null && !value.isBlank()) {
                attributes.put(key, value);
            }
            return this;
        }

        public Intent build() {
            return new Intent(
                id,
                operation,
                scope,
                destructive,
                privileged,
                networked,
                unrestricted,
                humanApproved,
                attributes
            );
        }
    }
}
