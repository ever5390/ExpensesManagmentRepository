
-- DROP DATABASE IF EXISTS db_expense_manager_dev;
-- CREATE DATABASE db_expense_manager_dev;

-- USE db_expense_manager_dev_v2;

INSERT INTO `owner` (`name`,`username`,`password`,`enabled`,`email`,`image`,`create_at`) VALUES ('Ever Rosales Peña','ever','$2a$10$7C7fdMxPpvx.FVR1Mj8bBuTBdkVsGAvBgPkUM.noBJRvP9iVLWQye',1,'everjrosalesp@gmail.com','https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/users%2Fever_1651713221827?alt=media&token=6f0bef77-a2d9-4ecd-855a-402b75be65b8','2010-02-02 00:00:00');

INSERT INTO `roles` (`name`) VALUES ('ROLE_ADMIN');
INSERT INTO `roles` (`name`) VALUES ('ROLE_USER');

INSERT INTO `owner_roles` (`owner_id`,`role_id`) VALUES (1, 1);
INSERT INTO `owner_roles` (`owner_id`,`role_id`) VALUES (1, 2);

INSERT INTO `type_workspace` (`id`, `type_name`) VALUES (1, 'SINGLE');
INSERT INTO `type_workspace` (`id`, `type_name`) VALUES (2, 'SHARED');

INSERT INTO `workspace` (`id`, `active`, `name`, `fk_owner_id`, `fk_type_id`) VALUES (1, 1, 'Workspace', 1, 1);

INSERT INTO `period` (`id`, `final_date`, `active`, `start_date`, `status_period`, `fk_workspace_id`) VALUES (1, '2023-03-30 00:00:00', true, '2023-01-01 00:00:00', true, 1);

INSERT INTO `account_type` (`id`, `type_name`) VALUES (1, 'PARENT');
INSERT INTO `account_type` (`id`, `type_name`) VALUES (2, 'CHILD');

INSERT INTO `financial_entity` (`id`, `enabled`,`image`, `name`, `fk_owner_id`) VALUES (1,true, "BCP_ICON_SVG", 'BCP', 1);
INSERT INTO `financial_entity` (`id`, `enabled`,`image`, `name`, `fk_owner_id`) VALUES (2,true, "BBVA_ICON_SVG", 'BBVA', 1);
INSERT INTO `financial_entity` (`id`, `enabled`,`image`, `name`, `fk_owner_id`) VALUES (3,true, "SCOTIABANK_ICON_SVG", 'SCOTIABANK', 1);
INSERT INTO `financial_entity` (`id`, `enabled`,`image`, `name`, `fk_owner_id`) VALUES (4,true, "INTERBANK_ICON_SVG", 'INTERBANK', 1);
INSERT INTO `financial_entity` (`id`, `enabled`,`image`, `name`, `fk_owner_id`) VALUES (5,true, "AGORA_ICON_SVG", 'AGORA', 1);
INSERT INTO `financial_entity` (`id`, `enabled`,`image`, `name`, `fk_owner_id`) VALUES (6,true, "SAGA_ICON_SVG", 'SAGA', 1);
INSERT INTO `financial_entity` (`id`, `enabled`,`image`, `name`, `fk_owner_id`) VALUES (7,true, "EFECTIVO_ICON_SVG", 'EFECTIVO', 1);

INSERT INTO `financial_entity_generic` (`id`, `enabled`,`image`, `name`) VALUES (1,true, "BCP_ICON_SVG", 'BCP');
INSERT INTO `financial_entity_generic` (`id`, `enabled`,`image`, `name`) VALUES (2,true, "BBVA_ICON_SVG", 'BBVA');
INSERT INTO `financial_entity_generic` (`id`, `enabled`,`image`, `name`) VALUES (3,true, "SCOTIABANK_ICON_SVG", 'SCOTIABANK');
INSERT INTO `financial_entity_generic` (`id`, `enabled`,`image`, `name`) VALUES (4,true, "INTERBANK_ICON_SVG", 'INTERBANK');
INSERT INTO `financial_entity_generic` (`id`, `enabled`,`image`, `name`) VALUES (5,true, "AGORA_ICON_SVG", 'AGORA');
INSERT INTO `financial_entity_generic` (`id`, `enabled`,`image`, `name`) VALUES (6,true, "SAGA_ICON_SVG", 'SAGA');
INSERT INTO `financial_entity_generic` (`id`, `enabled`,`image`, `name`) VALUES (7,true, "EFECTIVO_ICON_SVG", 'EFECTIVO');

INSERT INTO `type_card` (`id`, `enabled`, `name`, `fk_owner_id`) VALUES (1,true, "DEBIT", 1);
INSERT INTO `type_card` (`id`, `enabled`, `name`, `fk_owner_id`) VALUES (2,true, "CREDIT", 1);
INSERT INTO `type_card` (`id`, `enabled`, `name`, `fk_owner_id`) VALUES (3,true, "RECARGABLE", 1);
INSERT INTO `type_card` (`id`, `enabled`, `name`, `fk_owner_id`) VALUES (4,true, "EFECTIVO", 1);

INSERT INTO `type_card_generic` (`id`, `enabled`, `name`) VALUES (1,true, "DEBIT");
INSERT INTO `type_card_generic` (`id`, `enabled`, `name`) VALUES (2,true, "CREDIT");
INSERT INTO `type_card_generic` (`id`, `enabled`, `name`) VALUES (3,true, "RECARGABLE");
INSERT INTO `type_card_generic` (`id`, `enabled`, `name`) VALUES (4,true, "EFECTIVO");

INSERT INTO `group_category` (`id`, `name`) VALUES (1, 'GASTO FIJO');
INSERT INTO `group_category` (`id`, `name`) VALUES (2, 'GASTO VARIABLE');

INSERT INTO `sub_category` (`id`, `name`) VALUES (1, 'DEPORTES');
INSERT INTO `sub_category` (`id`, `name`) VALUES (2, 'DIVERSIÓN');

INSERT INTO `category_generic` (`id`, `active`, `image`, `name`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (1, 1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fluz.jpg?alt=media&token=2aa07c13-62d2-4ab3-bb9d-9d08bf9e8632', 'LUZ', 1, 1);
INSERT INTO `category_generic` (`id`, `active`, `image`, `name`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (2, 1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fagua_service.png?alt=media&token=84c2c043-9cbe-43f6-a244-22223ece7e8f', 'AGUA', 1, 1);
INSERT INTO `category_generic` (`id`, `active`, `image`, `name`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (3, 1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fmaintenance.png?alt=media&token=7c975f86-44fd-4ef1-824c-30a09dc99eca', 'MANTENIMIENTO', 1, 1);
INSERT INTO `category_generic` (`id`, `active`, `image`, `name`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (4, 1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fdepartment.png?alt=media&token=0612213c-1317-4e3c-9f2d-1a50433c7cde', 'DEPARTAMENTO', 1, 1);
INSERT INTO `category_generic` (`id`, `active`, `image`, `name`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (5, 1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fabono_renta.png?alt=media&token=dc3d7af5-f8c7-45f0-9e52-f6c3bb4f1cfc', 'ABONO_MADRE', 1, 1);
INSERT INTO `category_generic` (`id`, `active`, `image`, `name`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (6, 1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fmovil.png?alt=media&token=6093f4af-4f9a-41fe-a17f-67929369936d', 'MOVIL', 1, 1);
INSERT INTO `category_generic` (`id`, `active`, `image`, `name`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (7, 1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Finternet.png?alt=media&token=cf38e8af-7705-43de-9614-18ef55973a32', 'INTERNET Y TELF', 1, 1);
INSERT INTO `category_generic` (`id`, `active`, `image`, `name`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (8, 1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fgas.png?alt=media&token=2ea5c486-c637-4fc3-98ba-7ba355cb7a90', 'GAS', 1, 1);
INSERT INTO `category_generic` (`id`, `active`, `image`, `name`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (9, 1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fplan-estrategico.png?alt=media&token=3cc5ddba-f07a-45f1-8f62-a44abd55a55d', 'ABONO AHORRO', 1, 1);
INSERT INTO `category_generic` (`id`, `active`, `image`, `name`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (10,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fmarket.png?alt=media&token=7d603236-bcc7-4dda-9505-70035b62ee4c', 'ALIMENTOS MERCADO', 1, 1);
INSERT INTO `category_generic` (`id`, `active`, `image`, `name`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (11,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fmascota.png?alt=media&token=0c52a57f-ac8b-44aa-82bc-34dbaeddfd1d', 'COMIDA MASCOTA', 1, 1);
INSERT INTO `category_generic` (`id`, `active`, `image`, `name`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (12,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fveterinario.png?alt=media&token=ba1171f4-3ca8-4c25-8977-8693f9d16bfc', 'VETERINARIO', 1, 1);
INSERT INTO `category_generic` (`id`, `active`, `image`, `name`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (13,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Ftoyspet.png?alt=media&token=3298102f-8684-4359-b75d-73ef94bdb3d6', 'JUEGUETES MASCOTA', 1, 1);
INSERT INTO `category_generic` (`id`, `active`, `image`, `name`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (14,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fdesayuno.png?alt=media&token=3b331101-4a84-456f-a4f8-177b3efe60f6', 'DESAYUNO', 1, 1);
INSERT INTO `category_generic` (`id`, `active`, `image`, `name`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (15,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Falmuerzo.png?alt=media&token=226bfc80-59e3-4ea3-873a-b2f5f3f0dba7', 'ALMUERZO', 1, 1);
INSERT INTO `category_generic` (`id`, `active`, `image`, `name`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (16,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fcena.png?alt=media&token=410d5455-7d92-4e0d-b884-f753263f022d', 'CENA', 1, 1);
INSERT INTO `category_generic` (`id`, `active`, `image`, `name`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (17,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fgustitos.png?alt=media&token=bb8fde48-3e2c-45d6-b190-bf730ed34bb6', 'APERITIVOS', 1, 1);
INSERT INTO `category_generic` (`id`, `active`, `image`, `name`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (18,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fvisita.png?alt=media&token=d9b92a19-d7fd-407c-8f4f-ad9137fad412', 'VISITA', 1, 1);
INSERT INTO `category_generic` (`id`, `active`, `image`, `name`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (19,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fcarro.JPG?alt=media&token=f8a3578d-4ea1-4f59-b14f-3358e22d2fef', 'CARRO', 1, 1);
INSERT INTO `category_generic` (`id`, `active`, `image`, `name`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (20,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fothers.png?alt=media&token=e7216d12-49e6-40e1-a81c-64d9a38884e0', 'OTROS', 1, 1);
INSERT INTO `category_generic` (`id`, `active`, `image`, `name`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (21,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Frecreacion.JPG?alt=media&token=86088d2f-034e-4c75-86c6-77b94af97bc5', 'RECREACION', 1, 1);
INSERT INTO `category_generic` (`id`, `active`, `image`, `name`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (22,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fregalos.JPG?alt=media&token=9eecb5f7-3ae9-44a0-b6ed-786bb0e07de2', 'REGALOS', 1, 1);

INSERT INTO `category` (`id`, `active`, `image`, `name`, `fk_owner_id`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (1, 1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fluz.jpg?alt=media&token=2aa07c13-62d2-4ab3-bb9d-9d08bf9e8632', 'LUZ', 1, 1, 1);
INSERT INTO `category` (`id`, `active`, `image`, `name`, `fk_owner_id`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (2, 1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fagua_service.png?alt=media&token=84c2c043-9cbe-43f6-a244-22223ece7e8f', 'AGUA', 1, 1, 1);
INSERT INTO `category` (`id`, `active`, `image`, `name`, `fk_owner_id`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (3, 1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fmaintenance.png?alt=media&token=7c975f86-44fd-4ef1-824c-30a09dc99eca', 'MANTENIMIENTO', 1, 1, 1);
INSERT INTO `category` (`id`, `active`, `image`, `name`, `fk_owner_id`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (4, 1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fdepartment.png?alt=media&token=0612213c-1317-4e3c-9f2d-1a50433c7cde', 'DEPARTAMENTO', 1, 1, 1);
INSERT INTO `category` (`id`, `active`, `image`, `name`, `fk_owner_id`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (5, 1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fabono_renta.png?alt=media&token=dc3d7af5-f8c7-45f0-9e52-f6c3bb4f1cfc', 'ABONO_MADRE', 1, 1, 1);
INSERT INTO `category` (`id`, `active`, `image`, `name`, `fk_owner_id`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (6, 1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fmovil.png?alt=media&token=6093f4af-4f9a-41fe-a17f-67929369936d', 'MOVIL', 1, 1, 1);
INSERT INTO `category` (`id`, `active`, `image`, `name`, `fk_owner_id`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (7, 1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Finternet.png?alt=media&token=cf38e8af-7705-43de-9614-18ef55973a32', 'INTERNET Y TELF', 1, 1, 1);
INSERT INTO `category` (`id`, `active`, `image`, `name`, `fk_owner_id`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (8, 1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fgas.png?alt=media&token=2ea5c486-c637-4fc3-98ba-7ba355cb7a90', 'GAS', 1, 1, 1);
INSERT INTO `category` (`id`, `active`, `image`, `name`, `fk_owner_id`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (9, 1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fplan-estrategico.png?alt=media&token=3cc5ddba-f07a-45f1-8f62-a44abd55a55d', 'ABONO AHORRO', 1, 1, 1);
INSERT INTO `category` (`id`, `active`, `image`, `name`, `fk_owner_id`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (10,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fmarket.png?alt=media&token=7d603236-bcc7-4dda-9505-70035b62ee4c', 'ALIMENTOS MERCADO', 1, 1, 1);
INSERT INTO `category` (`id`, `active`, `image`, `name`, `fk_owner_id`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (11,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fmascota.png?alt=media&token=0c52a57f-ac8b-44aa-82bc-34dbaeddfd1d', 'COMIDA MASCOTA', 1, 1, 1);
INSERT INTO `category` (`id`, `active`, `image`, `name`, `fk_owner_id`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (12,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fveterinario.png?alt=media&token=ba1171f4-3ca8-4c25-8977-8693f9d16bfc', 'VETERINARIO', 1, 1, 1);
INSERT INTO `category` (`id`, `active`, `image`, `name`, `fk_owner_id`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (13,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Ftoyspet.png?alt=media&token=3298102f-8684-4359-b75d-73ef94bdb3d6', 'JUEGUETES MASCOTA', 1, 1, 1);
INSERT INTO `category` (`id`, `active`, `image`, `name`, `fk_owner_id`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (14,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fdesayuno.png?alt=media&token=3b331101-4a84-456f-a4f8-177b3efe60f6', 'DESAYUNO', 1, 1, 1);
INSERT INTO `category` (`id`, `active`, `image`, `name`, `fk_owner_id`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (15,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Falmuerzo.png?alt=media&token=226bfc80-59e3-4ea3-873a-b2f5f3f0dba7', 'ALMUERZO', 1, 1, 1);
INSERT INTO `category` (`id`, `active`, `image`, `name`, `fk_owner_id`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (16,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fcena.png?alt=media&token=410d5455-7d92-4e0d-b884-f753263f022d', 'CENA', 1, 1, 1);
INSERT INTO `category` (`id`, `active`, `image`, `name`, `fk_owner_id`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (17,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fgustitos.png?alt=media&token=bb8fde48-3e2c-45d6-b190-bf730ed34bb6', 'APERITIVOS', 1, 1, 1);
INSERT INTO `category` (`id`, `active`, `image`, `name`, `fk_owner_id`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (18,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fvisita.png?alt=media&token=d9b92a19-d7fd-407c-8f4f-ad9137fad412', 'VISITA', 1, 1, 1);
INSERT INTO `category` (`id`, `active`, `image`, `name`, `fk_owner_id`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (19,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fcarro.JPG?alt=media&token=f8a3578d-4ea1-4f59-b14f-3358e22d2fef', 'CARRO', 1, 1, 1);
INSERT INTO `category` (`id`, `active`, `image`, `name`, `fk_owner_id`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (20,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fothers.png?alt=media&token=e7216d12-49e6-40e1-a81c-64d9a38884e0', 'OTROS', 1, 1, 1);
INSERT INTO `category` (`id`, `active`, `image`, `name`, `fk_owner_id`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (21,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Frecreacion.JPG?alt=media&token=86088d2f-034e-4c75-86c6-77b94af97bc5', 'RECREACION', 1, 1, 1);
INSERT INTO `category` (`id`, `active`, `image`, `name`, `fk_owner_id`, `fk_group_category_id`, `fk_sub_category_id`) VALUES (22,1, 'https://firebasestorage.googleapis.com/v0/b/usuarios-8190a.appspot.com/o/categories%2Fregalos.JPG?alt=media&token=9eecb5f7-3ae9-44a0-b6ed-786bb0e07de2', 'REGALOS', 1, 1, 1);

INSERT INTO `account` (`id`, `account_name`,`account_number`, `account_parent_id`, `balance`, `balance_available`, `balance_only_initial`, `create_at`, `enabled`, `status_account`,`fk_accounttype_id`, `fk_financial_entity_id`, `fk_period_id`, `fk_type_card_id`) VALUES (1,'Saldo','529497332',1,1500,1000,1500,'2023-02-11 16:35:33.457000',1,'INITIAL',1,1,1,1);
INSERT INTO `account` (`id`, `account_name`,`account_number`, `account_parent_id`, `balance`, `balance_available`, `balance_only_initial`, `create_at`, `enabled`, `status_account`,`fk_accounttype_id`, `fk_financial_entity_id`, `fk_period_id`, `fk_type_card_id`) VALUES (2,'Budget 11','145597029',1,100,100,100,'2023-02-11 16:35:50.167000',1,'INITIAL',2,1,1,1);
INSERT INTO `account` (`id`, `account_name`,`account_number`, `account_parent_id`, `balance`, `balance_available`, `balance_only_initial`, `create_at`, `enabled`, `status_account`,`fk_accounttype_id`, `fk_financial_entity_id`, `fk_period_id`, `fk_type_card_id`) VALUES (3,'Sustento','171854232',3,700,100,700,'2023-02-11 16:36:08.048000',1,'INITIAL',1,2,1,1);
INSERT INTO `account` (`id`, `account_name`,`account_number`, `account_parent_id`, `balance`, `balance_available`, `balance_only_initial`, `create_at`, `enabled`, `status_account`,`fk_accounttype_id`, `fk_financial_entity_id`, `fk_period_id`, `fk_type_card_id`) VALUES (4,'Budget 11','268890991',3,500,500,500,'2023-02-11 16:36:26.848000',1,'INITIAL',2,2,1,1);
INSERT INTO `account` (`id`, `account_name`,`account_number`, `account_parent_id`, `balance`, `balance_available`, `balance_only_initial`, `create_at`, `enabled`, `status_account`,`fk_accounttype_id`, `fk_financial_entity_id`, `fk_period_id`, `fk_type_card_id`) VALUES (5,'Budget 2','738516868',3,100,100,100,'2023-02-11 16:36:55.799000',1,'INITIAL',2,2,1,1);
INSERT INTO `account` (`id`, `account_name`,`account_number`, `account_parent_id`, `balance`, `balance_available`, `balance_only_initial`, `create_at`, `enabled`, `status_account`,`fk_accounttype_id`, `fk_financial_entity_id`, `fk_period_id`, `fk_type_card_id`) VALUES (6,'Budget 2','951010038',1,400,400,400,'2023-02-11 16:37:13.910000',1,'INITIAL',2,1,1,1);
INSERT INTO `account` (`id`, `account_name`,`account_number`, `account_parent_id`, `balance`, `balance_available`, `balance_only_initial`, `create_at`, `enabled`, `status_account`,`fk_accounttype_id`, `fk_financial_entity_id`, `fk_period_id`, `fk_type_card_id`) VALUES (7,'Respaldo','452711094',7,1200,1200,1200,'2023-02-11 16:37:41.746000',1,'INITIAL',1,3,1,1);