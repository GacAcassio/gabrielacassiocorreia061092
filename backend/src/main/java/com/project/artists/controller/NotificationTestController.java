// package com.project.artists.controller;

// import com.project.artists.dto.notification.NotificationDTO;
// import com.project.artists.dto.notification.NotificationType;
// import com.project.artists.service.NotificationService;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.HashMap;
// import java.util.Map;

// /**
//  * Controller de teste para WebSocket/Notificações
//  */
// @RestController
// @RequestMapping("/test")
// public class NotificationTestController {
    
//     private static final Logger logger = LoggerFactory.getLogger(NotificationTestController.class);
    
//     @Autowired
//     private NotificationService notificationService;
    
//     /**
//      * Envia notificação de teste via WebSocket
//      * Endpoint PÚBLICO para testar se notificações funcionam
//      */
//     @PostMapping("/notification")
//     public ResponseEntity<Map<String, Object>> sendTestNotification(
//             @RequestParam(defaultValue = "Teste") String title,
//             @RequestParam(defaultValue = "Esta é uma notificação de teste") String message
//     ) {
//         logger.info("Enviando notificação de teste: {} - {}", title, message);
        
//         try {
//             // Envia notificação
//             notificationService.sendNotification(
//                 NotificationType.SYSTEM_NOTIFICATION,
//                 title,
//                 message,
//                 Map.of("timestamp", System.currentTimeMillis())
//             );
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "Notificação enviada com sucesso");
//             response.put("title", title);
//             response.put("content", message);
            
//             logger.info("Notificação de teste enviada com sucesso");
            
//             return ResponseEntity.ok(response);
            
//         } catch (Exception e) {
//             logger.error("Erro ao enviar notificação de teste: {}", e.getMessage(), e);
            
//             Map<String, Object> error = new HashMap<>();
//             error.put("success", false);
//             error.put("error", e.getMessage());
            
//             return ResponseEntity.internalServerError().body(error);
//         }
//     }
    
//     /**
//      * Verifica status do WebSocket
//      */
//     @GetMapping("/websocket/status")
//     public ResponseEntity<Map<String, Object>> getWebSocketStatus() {
//         Map<String, Object> status = new HashMap<>();
//         status.put("websocket", "configured");
//         status.put("endpoint", "/ws");
//         status.put("transports", new String[]{"websocket", "xhr-polling", "xhr-streaming"});
//         status.put("broker", new String[]{"/topic", "/queue"});
//         status.put("appPrefix", "/app");
        
//         return ResponseEntity.ok(status);
//     }
// }

