INSERT INTO category (id, name, parent_id) VALUES ('4220f24cd59f4ef6b2b07f74a1e264bd', 'root category', '4220f24cd59f4ef6b2b07f74a1e264bd'); 
INSERT INTO category (id, name, parent_id) VALUES ('33a9a87811a04743966cddb5e82e1fb7', 'internet-of-things', '4220f24cd59f4ef6b2b07f74a1e264bd'); 
INSERT INTO category (id, name, parent_id) VALUES ('86b90e8171ce49c8a9a487b31c818a7a', 'smart-home', '33a9a87811a04743966cddb5e82e1fb7'); 
INSERT INTO category (id, name, parent_id) VALUES ('253ef1ab7191486c8c410b3889ac2441', 'transportation', '33a9a87811a04743966cddb5e82e1fb7'); 
INSERT INTO category (id, name, parent_id) VALUES ('90f26db670434fe99da395ec1652d784', 'health', '33a9a87811a04743966cddb5e82e1fb7'); 
INSERT INTO category (id, name, parent_id) VALUES ('a5b108addf094fbda7d20270538d4e1c', 'heating-controll', '86b90e8171ce49c8a9a487b31c818a7a'); 
INSERT INTO category (id, name, parent_id) VALUES ('f47754d0c689443ebc1bbf0cd2c8bd86', 'energy-monitoring', '86b90e8171ce49c8a9a487b31c818a7a'); 

INSERT INTO platform (id, name) VALUES ('d0d5de5e30984d2bb7366dd8b9f8208a', 'Arduino'); 
INSERT INTO platform (id, name) VALUES ('261353b9431c440eaf27a9b2b66771aa', 'Linux 64-bit'); 
INSERT INTO platform (id, name) VALUES ('0b9ac81573e543d6846e6fdc1b6f4c5f', 'Raspberry Pi'); 

INSERT INTO installation_method (id, name) VALUES ('699317adf4204e43987911e9b5ed457a', 'Docker'); 
INSERT INTO installation_method (id, name) VALUES ('ecc15697061d45e8a6b28afb4bc504e5', 'Bluetooth'); 
INSERT INTO installation_method_property (installation_method_id, property_name) VALUES ('699317adf4204e43987911e9b5ed457a', 'docker_hub_repo'); 
INSERT INTO manager_type (id, name, platform_id, installation_method_id, cardinality, installation_instructions, installation_url) VALUES ('3acc87776601456f81724b52d7bd0d6f', 'MCSS for Arduino', 'd0d5de5e30984d2bb7366dd8b9f8208a', 'ecc15697061d45e8a6b28afb4bc504e5', 'ALL', NULL, NULL); 
INSERT INTO manager_type (id, name, platform_id, installation_method_id, cardinality, installation_instructions, installation_url) VALUES ('9776bbb25d654718bfec31db631a58ff', 'Ahab for Linux 64-bit', '261353b9431c440eaf27a9b2b66771aa', '699317adf4204e43987911e9b5ed457a', 'ONE', 'Run this in your RPi console: wget -qO-  {{url}} | sudo sh', '{{server}}/resources/downloads/ahab_installation?manager_id={{id}}&manager_key={{key}}'); 

INSERT INTO user (id, name, username, password) VALUES ('09ef12c4c10643b5a909fd3ad509e967', 'Simon Stastny', 'stastny.simon@gmail.com', '$2a$10$zK.Gk1OulqlvClHadnX/Ie/CI7Sn7uYlul8zxgfFIcoCygVmv2D7O'); 
INSERT INTO user (id, name, username, password) VALUES ('0bd61c9a6ce44c65ab33fde18d2f33bd', 'Nell Meyer', 'porttitor@enim.edu', '$2a$10$LdpQGeodvEV8VKXOBZ28QOGy9kDJ4Stbw6nW97l6n1QBpkYdU37y2'); 
INSERT INTO user (id, name, username, password) VALUES ('0e1eca25458142c49178e140eff942f1', 'Dalton Calhoun', 'nunc@arcu.co.uk', '$2a$10$NAHIyYTNvRdS90z/QDLaC.A8ce8mYWoF1WUMnuHZE2ioL6UCDYHSq'); 
INSERT INTO user (id, name, username, password) VALUES ('2cfc4350c42d425db11a0bb13874c8a6', 'Clio Hendrix', 'tristique@acurna.net', '$2a$10$/7oKZFwoHGZAKarRu4A5Suxlev59zMzOTh/oidu9VhuPaUl81Dp0G'); 
INSERT INTO user (id, name, username, password) VALUES ('4a5b17a51f7848158566af3e6bcc1e7c', 'Dominique Rodriquez', 'adipiscing.ligula@semper.edu', '$2a$10$W60kBi1phPnY9sAfTKrFNempz6cpDq2Z1H2zrmCBM7mFSl5GVjt7.'); 
INSERT INTO user_group (group_name, user_id) VALUES ('user', '09ef12c4c10643b5a909fd3ad509e967'); 
INSERT INTO user_group (group_name, user_id) VALUES ('developer', '09ef12c4c10643b5a909fd3ad509e967'); 
INSERT INTO user_group (group_name, user_id) VALUES ('admin', '09ef12c4c10643b5a909fd3ad509e967'); 

INSERT INTO app (id, name, description, platform_id, author_id) VALUES ('8978ab096dbb4b56b058da804ce46475', 'nginx for Linux x64', 'just an nginx server', '261353b9431c440eaf27a9b2b66771aa', '09ef12c4c10643b5a909fd3ad509e967'); 
INSERT INTO app_property (app_id, property_name, property_value) VALUES ('8978ab096dbb4b56b058da804ce46475', 'docker_hub_repo', 'nginx'); 

INSERT INTO device (id, name, platform_id, owner_id) VALUES ('8204d02212fb4807b68243bbf4f19687', 'Simons ThinkPad', '261353b9431c440eaf27a9b2b66771aa', '09ef12c4c10643b5a909fd3ad509e967'); 
INSERT INTO manager (id, name, owner_id, manager_type_id, `key`, installed) VALUES ('37b55bc986e84c118270284511c05fd2', 'MSCC on HTC Evo 3D', '09ef12c4c10643b5a909fd3ad509e967', '3acc87776601456f81724b52d7bd0d6f', NULL, false); 
INSERT INTO manager (id, name, owner_id, manager_type_id, `key`, installed) VALUES ('45547f8d130e42909a24528d2f4e07a3', 'Ahab on ThinkPad', '09ef12c4c10643b5a909fd3ad509e967', '9776bbb25d654718bfec31db631a58ff', '1b122520431a49f69891aa81c97cf5fa', false); 
INSERT INTO managed_device (manager_id, device_id) VALUES ('45547f8d130e42909a24528d2f4e07a3', '8204d02212fb4807b68243bbf4f19687'); 

INSERT INTO installation (id, app_id, device_id) VALUES ('382c71546fe2496bbfabbfa90d663975', '8978ab096dbb4b56b058da804ce46475', '8204d02212fb4807b68243bbf4f19687'); 
INSERT INTO installation_property (installation_id, property_name, property_value) VALUES ('382c71546fe2496bbfabbfa90d663975', 'ports', 'PortMapping{privatePort=443, publicPort=49154, type=tcp, ip=0.0.0.0}PortMapping{privatePort=80, publicPort=49155, type=tcp, ip=0.0.0.0}'); 

COMMIT;