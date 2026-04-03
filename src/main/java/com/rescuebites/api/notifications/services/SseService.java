package com.rescuebites.api.notifications.services;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseService {

    private final Map<UUID, List<SseEmitter>> clientEmitters = new ConcurrentHashMap<>();
    private final Map<UUID, List<SseEmitter>> commerceEmitters = new ConcurrentHashMap<>();

    // ================= SUBSCRIBE =================

    public SseEmitter subscribeClient(UUID clientId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        clientEmitters
                .computeIfAbsent(clientId, k -> new CopyOnWriteArrayList<>())
                .add(emitter);

        emitter.onCompletion(() -> removeClientEmitter(clientId, emitter));
        emitter.onTimeout(() -> removeClientEmitter(clientId, emitter));

        return emitter;
    }

    public SseEmitter subscribeCommerce(UUID commerceId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        commerceEmitters
                .computeIfAbsent(commerceId, k -> new CopyOnWriteArrayList<>())
                .add(emitter);

        emitter.onCompletion(() -> removeCommerceEmitter(commerceId, emitter));
        emitter.onTimeout(() -> removeCommerceEmitter(commerceId, emitter));

        return emitter;
    }

    // ================= SEND =================

    public void sendToClient(UUID clientId, Object data) {
        send(clientEmitters.get(clientId), "notification", data);
    }

    public void sendToCommerce(UUID commerceId, Object data) {
        send(commerceEmitters.get(commerceId), "notification", data);
    }

    private void send(List<SseEmitter> emitters, String eventName, Object data) {
        if (emitters == null) return;

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(data));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }

    // ================= CLEAN =================

    private void removeClientEmitter(UUID clientId, SseEmitter emitter) {
        List<SseEmitter> list = clientEmitters.get(clientId);
        if (list != null) list.remove(emitter);
    }

    private void removeCommerceEmitter(UUID commerceId, SseEmitter emitter) {
        List<SseEmitter> list = commerceEmitters.get(commerceId);
        if (list != null) list.remove(emitter);
    }
}
