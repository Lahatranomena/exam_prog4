package com.exam.demo.endpoint.event.consumer.model;

import com.exam.demo.PojaGenerated;
import com.exam.demo.endpoint.event.model.PojaEvent;

@PojaGenerated
public record TypedEvent(String typeName, PojaEvent payload) {}
