package com.joklek.vinted.model;

import java.util.Objects;
import java.util.Optional;

public final class SuccessOrRaw<S> {

    private final S success;
    private final String error;

    private SuccessOrRaw(S success, String error) {
        this.success = success;
        this.error = error;
    }

    public static <S> SuccessOrRaw<S> success(S success) {
        return new SuccessOrRaw<>(success, null);
    }

    public static SuccessOrRaw<?> error(String error) {
        return new SuccessOrRaw<>(null, error);
    }

    public Optional<S> getSuccess() {
        return Optional.ofNullable(this.success);
    }

    public Optional<String> getError() {
        return Optional.ofNullable(this.error);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (SuccessOrRaw) obj;
        return Objects.equals(this.success, that.success) &&
                Objects.equals(this.error, that.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.success, this.error);
    }

    @Override
    public String toString() {
        return "SuccessOrRaw{" +
                "success=" + this.success +
                ", error='" + this.error + '\'' +
                '}';
    }
}
