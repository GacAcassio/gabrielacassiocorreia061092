package com.project.artists.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuração para habilitar agendamento de tarefas
 * 
 * Permite o uso de @Scheduled em qualquer componente
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    // Spring irá detectar e executar métodos anotados com @Scheduled
}
