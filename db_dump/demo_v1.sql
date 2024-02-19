PGDMP     %                    |            postgres    15.4    15.4     �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            �           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            �           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            �           1262    5    postgres    DATABASE     |   CREATE DATABASE postgres WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Russian_Russia.1251';
    DROP DATABASE postgres;
                postgres    false            �           0    0    DATABASE postgres    COMMENT     N   COMMENT ON DATABASE postgres IS 'default administrative connection database';
                   postgres    false    3326                        3079    16929 	   adminpack 	   EXTENSION     A   CREATE EXTENSION IF NOT EXISTS adminpack WITH SCHEMA pg_catalog;
    DROP EXTENSION adminpack;
                   false                        0    0    EXTENSION adminpack    COMMENT     M   COMMENT ON EXTENSION adminpack IS 'administrative functions for PostgreSQL';
                        false    2            �            1259    17071    repair_requests    TABLE       CREATE TABLE public.repair_requests (
    request_number integer NOT NULL,
    client_name character varying(50),
    client_phone character varying(30),
    equipment_serial_number character varying(20),
    equipment_type character varying(50),
    description text,
    notes text
);
 #   DROP TABLE public.repair_requests;
       public         heap    postgres    false            �            1259    17070 "   repair_requests_request_number_seq    SEQUENCE     �   CREATE SEQUENCE public.repair_requests_request_number_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 9   DROP SEQUENCE public.repair_requests_request_number_seq;
       public          postgres    false    216                       0    0 "   repair_requests_request_number_seq    SEQUENCE OWNED BY     i   ALTER SEQUENCE public.repair_requests_request_number_seq OWNED BY public.repair_requests.request_number;
          public          postgres    false    215            f           2604    17074    repair_requests request_number    DEFAULT     �   ALTER TABLE ONLY public.repair_requests ALTER COLUMN request_number SET DEFAULT nextval('public.repair_requests_request_number_seq'::regclass);
 M   ALTER TABLE public.repair_requests ALTER COLUMN request_number DROP DEFAULT;
       public          postgres    false    215    216    216            �          0    17071    repair_requests 
   TABLE DATA           �   COPY public.repair_requests (request_number, client_name, client_phone, equipment_serial_number, equipment_type, description, notes) FROM stdin;
    public          postgres    false    216                     0    0 "   repair_requests_request_number_seq    SEQUENCE SET     Q   SELECT pg_catalog.setval('public.repair_requests_request_number_seq', 10, true);
          public          postgres    false    215            h           2606    17078 $   repair_requests repair_requests_pkey 
   CONSTRAINT     n   ALTER TABLE ONLY public.repair_requests
    ADD CONSTRAINT repair_requests_pkey PRIMARY KEY (request_number);
 N   ALTER TABLE ONLY public.repair_requests DROP CONSTRAINT repair_requests_pkey;
       public            postgres    false    216            �   �  x��VIn�@<���c���������h�b��m	`Y� `$Ѧ�P_��Q�jHS�i+� :�dOOWuuʹ���ml�6�sS<���6u�o�ը�{mZ�N���A�����~�~����)����nݩ�7v�Ǎݹ3w�O�3.1ȷBع�����b�Em�7��;q3��c��(f����Taʤ����/ś��ؕ��s����"c������%��E�?�I�����S�+���.��ݰ?���|I��_�_����.�-�7��y�6�*�������f��!�ؒ�2QQ1��j�ˬ�X
��U�-i�oI��q�iJ܇��;aƄ�@L졾��".��
e%�j��˼��B:�G8i�O�p���)�i�_b菛�
�C�!���~8X܇]�+��'�p�Cv����ꂥJ��A"���P�d��GDA���"�^	��/"A]Q^`8e|�F��]5=	�G�%ĆP"��Nr��?^�e�S�`�:�Ͼeع���ς�<#!3*5`� ��2�Hʌ��h����# Aaw����G�}H=�����Y�^���O�@�䱎zQ�l�97l���Ý�j(G��gB���&���q�}<1��"�6u�*8V�NԄu)Frꠘ̫!���MFvP߼�w\�>��.]�h%}�)��i�Z�����fjU;`l���u�#�� F��!��|$�
ue�rMYo%ǌ�y��+Z��C�����TK�g��p���Gn���Y�(j 9��Ξ��O�CS�����>tՌb�:w�'�U36ϸA+�4��K���PҬ�QE.Ci%�t�Z�a�7�jN/ ��d����G�x���Yş��(�"$�]y)��ۑ3RWq/D!���Z�}ސ��:����;Ȯn��m��
�)�i��4D�h��-T,-��I�硦�(h5i~I��q�y}�*�j<. F���/����06���Jt����|�_�'˵sY��%�?x�(�����     