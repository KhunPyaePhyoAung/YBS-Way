package me.khun.ybsway.provider;

import java.util.function.Consumer;

public abstract class ObjectProvider<T> {
    private Consumer<T> consumer;

    protected final void provide(T obj) {
        if (consumer != null) {
            consumer.accept(obj);
        }
    }

    public void setConsumer(Consumer<T> consumer) {
        this.consumer = consumer;
    }
}
