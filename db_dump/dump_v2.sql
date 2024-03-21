PGDMP     4                    |            postgres    15.4    15.4 #               0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false                       0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false                       0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false                       1262    5    postgres    DATABASE     |   CREATE DATABASE postgres WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Russian_Russia.1251';
    DROP DATABASE postgres;
                postgres    false                       0    0    DATABASE postgres    COMMENT     N   COMMENT ON DATABASE postgres IS 'default administrative connection database';
                   postgres    false    3356                        3079    16929 	   adminpack 	   EXTENSION     A   CREATE EXTENSION IF NOT EXISTS adminpack WITH SCHEMA pg_catalog;
    DROP EXTENSION adminpack;
                   false                       0    0    EXTENSION adminpack    COMMENT     M   COMMENT ON EXTENSION adminpack IS 'administrative functions for PostgreSQL';
                        false    2            �            1259    33135    drugs    TABLE     B  CREATE TABLE public.drugs (
    drug_id integer NOT NULL,
    name character varying(255),
    manufacturer character varying(255),
    packaging_quantity integer,
    measure_unit character varying(10),
    dosage_mg character varying(50),
    in_price numeric(10,2),
    out_price numeric(10,2),
    in_stock integer
);
    DROP TABLE public.drugs;
       public         heap    postgres    false            �            1259    33134    drugs_drug_id_seq    SEQUENCE     �   CREATE SEQUENCE public.drugs_drug_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 (   DROP SEQUENCE public.drugs_drug_id_seq;
       public          postgres    false    220                       0    0    drugs_drug_id_seq    SEQUENCE OWNED BY     G   ALTER SEQUENCE public.drugs_drug_id_seq OWNED BY public.drugs.drug_id;
          public          postgres    false    219            �            1259    33128 	   employees    TABLE     �   CREATE TABLE public.employees (
    id integer NOT NULL,
    name character varying(100),
    login character varying(20),
    pass character varying(50),
    role character varying(20)
);
    DROP TABLE public.employees;
       public         heap    postgres    false            �            1259    33127    employees_id_seq    SEQUENCE     �   CREATE SEQUENCE public.employees_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 '   DROP SEQUENCE public.employees_id_seq;
       public          postgres    false    218                        0    0    employees_id_seq    SEQUENCE OWNED BY     E   ALTER SEQUENCE public.employees_id_seq OWNED BY public.employees.id;
          public          postgres    false    217            �            1259    33144    records    TABLE     �   CREATE TABLE public.records (
    transaction_id integer NOT NULL,
    drug character varying(255),
    packaging_quantity integer,
    total numeric(10,2),
    transaction_datetime timestamp without time zone
);
    DROP TABLE public.records;
       public         heap    postgres    false            �            1259    33143    records_transaction_id_seq    SEQUENCE     �   CREATE SEQUENCE public.records_transaction_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 1   DROP SEQUENCE public.records_transaction_id_seq;
       public          postgres    false    222            !           0    0    records_transaction_id_seq    SEQUENCE OWNED BY     Y   ALTER SEQUENCE public.records_transaction_id_seq OWNED BY public.records.transaction_id;
          public          postgres    false    221            �            1259    24951    repair_requests    TABLE     �  CREATE TABLE public.repair_requests (
    request_number integer NOT NULL,
    client_name character varying(50),
    client_phone character varying(30),
    equipment_serial_number character varying(20),
    equipment_type character varying(50),
    description text,
    notes text,
    state character varying(15),
    repairer character varying(50),
    priority character varying(15),
    register_date date,
    finish_date date
);
 #   DROP TABLE public.repair_requests;
       public         heap    postgres    false            �            1259    24950 "   repair_requests_request_number_seq    SEQUENCE     �   CREATE SEQUENCE public.repair_requests_request_number_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 9   DROP SEQUENCE public.repair_requests_request_number_seq;
       public          postgres    false    216            "           0    0 "   repair_requests_request_number_seq    SEQUENCE OWNED BY     i   ALTER SEQUENCE public.repair_requests_request_number_seq OWNED BY public.repair_requests.request_number;
          public          postgres    false    215            w           2604    33138    drugs drug_id    DEFAULT     n   ALTER TABLE ONLY public.drugs ALTER COLUMN drug_id SET DEFAULT nextval('public.drugs_drug_id_seq'::regclass);
 <   ALTER TABLE public.drugs ALTER COLUMN drug_id DROP DEFAULT;
       public          postgres    false    219    220    220            v           2604    33131    employees id    DEFAULT     l   ALTER TABLE ONLY public.employees ALTER COLUMN id SET DEFAULT nextval('public.employees_id_seq'::regclass);
 ;   ALTER TABLE public.employees ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    218    217    218            x           2604    33147    records transaction_id    DEFAULT     �   ALTER TABLE ONLY public.records ALTER COLUMN transaction_id SET DEFAULT nextval('public.records_transaction_id_seq'::regclass);
 E   ALTER TABLE public.records ALTER COLUMN transaction_id DROP DEFAULT;
       public          postgres    false    221    222    222            u           2604    24954    repair_requests request_number    DEFAULT     �   ALTER TABLE ONLY public.repair_requests ALTER COLUMN request_number SET DEFAULT nextval('public.repair_requests_request_number_seq'::regclass);
 M   ALTER TABLE public.repair_requests ALTER COLUMN request_number DROP DEFAULT;
       public          postgres    false    215    216    216                      0    33135    drugs 
   TABLE DATA           �   COPY public.drugs (drug_id, name, manufacturer, packaging_quantity, measure_unit, dosage_mg, in_price, out_price, in_stock) FROM stdin;
    public          postgres    false    220   .(                 0    33128 	   employees 
   TABLE DATA           @   COPY public.employees (id, name, login, pass, role) FROM stdin;
    public          postgres    false    218   �)                 0    33144    records 
   TABLE DATA           h   COPY public.records (transaction_id, drug, packaging_quantity, total, transaction_datetime) FROM stdin;
    public          postgres    false    222   �*                 0    24951    repair_requests 
   TABLE DATA           �   COPY public.repair_requests (request_number, client_name, client_phone, equipment_serial_number, equipment_type, description, notes, state, repairer, priority, register_date, finish_date) FROM stdin;
    public          postgres    false    216   �*       #           0    0    drugs_drug_id_seq    SEQUENCE SET     @   SELECT pg_catalog.setval('public.drugs_drug_id_seq', 10, true);
          public          postgres    false    219            $           0    0    employees_id_seq    SEQUENCE SET     >   SELECT pg_catalog.setval('public.employees_id_seq', 8, true);
          public          postgres    false    217            %           0    0    records_transaction_id_seq    SEQUENCE SET     I   SELECT pg_catalog.setval('public.records_transaction_id_seq', 1, false);
          public          postgres    false    221            &           0    0 "   repair_requests_request_number_seq    SEQUENCE SET     Q   SELECT pg_catalog.setval('public.repair_requests_request_number_seq', 10, true);
          public          postgres    false    215            ~           2606    33142    drugs drugs_pkey 
   CONSTRAINT     S   ALTER TABLE ONLY public.drugs
    ADD CONSTRAINT drugs_pkey PRIMARY KEY (drug_id);
 :   ALTER TABLE ONLY public.drugs DROP CONSTRAINT drugs_pkey;
       public            postgres    false    220            |           2606    33133    employees employees_pkey 
   CONSTRAINT     V   ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_pkey PRIMARY KEY (id);
 B   ALTER TABLE ONLY public.employees DROP CONSTRAINT employees_pkey;
       public            postgres    false    218            �           2606    33149    records records_pkey 
   CONSTRAINT     ^   ALTER TABLE ONLY public.records
    ADD CONSTRAINT records_pkey PRIMARY KEY (transaction_id);
 >   ALTER TABLE ONLY public.records DROP CONSTRAINT records_pkey;
       public            postgres    false    222            z           2606    24958 $   repair_requests repair_requests_pkey 
   CONSTRAINT     n   ALTER TABLE ONLY public.repair_requests
    ADD CONSTRAINT repair_requests_pkey PRIMARY KEY (request_number);
 N   ALTER TABLE ONLY public.repair_requests DROP CONSTRAINT repair_requests_pkey;
       public            postgres    false    216               P  x��RQJ1�~9EOP6IW�w�0n�⇠��hQ���X�5{�ɍ���V��,��f�̼��g��)��8�;4������o��BR�Bʢ[C-e*�7Np�E<C��ϱƇ��X�K>�xV��T�/�r���c�"7J�`�nc��=�����oF��h�<`K'h���4�ޚZ:lxQ*e�RLI)�$��~I�K�ؽGa.�}��9ҡ!�]�L�_*$�9�9+ͱ�M㈓�u���\�ﶓ͹�T�'B�iIs���k���j��V��b�)fB���:��צ\�φYDte��lTԣ�Ⴞ�9�I�؜�1��rY�         $  x�]�]J�@��'��
���-Ɨ�h�0ѷZAQQ�;1�ik��3;�ޙI��m&s�9�G�P��U�]���h�H�e&ÑHe&�JGC���ۙ��x����U�{�#�o4��	Se�g�j�a���s�u��'!�L��i��,�::���_Zq���F�n�������L@�L�Bߺ�r�h�9aޛ[�xs�gA�/pj��6�T��W4��i�(;�(/�n�[c��u��~��{2j���[�4�}���J��a�y￲�'J޲OLÜ5Ex��-�:9p�8����x:�            x������ � �           x���Kn�@D��)������s	o��a���aF�� A@K�ESu�����4E���	��H�4{�TW��37�ٚ�����߿kb��{;�ߌڽ#����^�x�'����=�݄vb�^�s��g��7	.7fg��Ev̸�G�a�3�Gv�����?�ؤ�����?V!��b����G�L�X������ؙ�`�����7�ȲB`j��wy�{�DL�ۉ���UTz��<�XHh/��'O�F�3���%
v����C�j�ōY�Y�g����� ���`����j��s5�|�ZCR����1�a%,Ǫ��[?2���~�x��[�xH��k7���u�c��+�LPmD�̼�*+�#1� ��D�]*�S2��o����B/+$ܐ���>�"0cQ
�]�\�����PX�T�yF�#Hr�I�1�2�R��@ �dm�U��@���-Pf�OOl�fx�l��C��K�:�S'҈��U;��o?�xv�+J�C+Sg�X��5�g������-�� ,��b�AH��$��>"{=1x�-�{�L�S�Q?d/���@����NO���ށz�RPR)'Z���
�M�8pTK5�F������"OF�"�l��cF�":��\���Pi��QE��i���rM=P}?3Y�yWg$}�hρ��;{�tgϰ�\j�{ ��F(��kNq����Vg��y�Ϭ����� �MZdX�x��o͇s{��]nƒ��RV���X��nE�ܔq��c%�=�ы��\0+y4U���GJ�s��俹�H���ε�n>�֚���e#��/X#���]Ղ�"qNLvc>�x2�i��! �w�����.�n��:jᵚ�ۨ<گ��l�)�`���pa\�+M�����?E�E+��y��R-��nҚ��s���W����.Gw��G��o�M��vS��շ��q9�թ�5{%<~��u��5~S�U��W��2��{�����:KN�������~x�������z�w(�;�ahqNnw*�'�����v7hv�v���j�?n4��[�     