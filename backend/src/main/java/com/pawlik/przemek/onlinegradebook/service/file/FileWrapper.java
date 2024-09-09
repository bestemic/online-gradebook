package com.pawlik.przemek.onlinegradebook.service.file;

import org.springframework.core.io.Resource;

public record FileWrapper(Resource resource, String fileName) {
}
