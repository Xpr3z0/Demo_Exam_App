PGDMP     (                    |            postgres    15.4    15.4 P    _           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            `           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            a           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            b           1262    5    postgres    DATABASE     |   CREATE DATABASE postgres WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Russian_Russia.1251';
    DROP DATABASE postgres;
                postgres    false            c           0    0    DATABASE postgres    COMMENT     N   COMMENT ON DATABASE postgres IS 'default administrative connection database';
                   postgres    false    3426                        2615    2200    public    SCHEMA        CREATE SCHEMA public;
    DROP SCHEMA public;
                pg_database_owner    false            d           0    0    SCHEMA public    COMMENT     6   COMMENT ON SCHEMA public IS 'standard public schema';
                   pg_database_owner    false    5            �            1259    33426    analyzes    TABLE     �   CREATE TABLE public.analyzes (
    id integer NOT NULL,
    request_id integer,
    processing_time character varying(100),
    expenses numeric,
    profit numeric,
    exe_quality numeric
);
    DROP TABLE public.analyzes;
       public         heap    postgres    false    5            �            1259    33425    analyzes_id_seq    SEQUENCE     �   CREATE SEQUENCE public.analyzes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 &   DROP SEQUENCE public.analyzes_id_seq;
       public          postgres    false    218    5            e           0    0    analyzes_id_seq    SEQUENCE OWNED BY     C   ALTER SEQUENCE public.analyzes_id_seq OWNED BY public.analyzes.id;
          public          postgres    false    217            �            1259    33534    assignments    TABLE     �   CREATE TABLE public.assignments (
    id integer NOT NULL,
    id_request integer,
    member_id integer,
    is_responsible boolean
);
    DROP TABLE public.assignments;
       public         heap    postgres    false    5            �            1259    33533    assignments_id_seq    SEQUENCE     �   CREATE SEQUENCE public.assignments_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 )   DROP SEQUENCE public.assignments_id_seq;
       public          postgres    false    227    5            f           0    0    assignments_id_seq    SEQUENCE OWNED BY     I   ALTER SEQUENCE public.assignments_id_seq OWNED BY public.assignments.id;
          public          postgres    false    226            �            1259    33553    drugs    TABLE     B  CREATE TABLE public.drugs (
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
       public         heap    postgres    false    5            �            1259    33552    drugs_drug_id_seq    SEQUENCE     �   CREATE SEQUENCE public.drugs_drug_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 (   DROP SEQUENCE public.drugs_drug_id_seq;
       public          postgres    false    5    229            g           0    0    drugs_drug_id_seq    SEQUENCE OWNED BY     G   ALTER SEQUENCE public.drugs_drug_id_seq OWNED BY public.drugs.drug_id;
          public          postgres    false    228            �            1259    33562 	   employees    TABLE     �   CREATE TABLE public.employees (
    employee_id integer NOT NULL,
    last_name character varying(50),
    first_name character varying(50),
    patronymic character varying(50),
    phone character varying(20)
);
    DROP TABLE public.employees;
       public         heap    postgres    false    5            �            1259    33561    employees_employee_id_seq    SEQUENCE     �   CREATE SEQUENCE public.employees_employee_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 0   DROP SEQUENCE public.employees_employee_id_seq;
       public          postgres    false    5    231            h           0    0    employees_employee_id_seq    SEQUENCE OWNED BY     W   ALTER SEQUENCE public.employees_employee_id_seq OWNED BY public.employees.employee_id;
          public          postgres    false    230            �            1259    33460    members    TABLE     �   CREATE TABLE public.members (
    id integer NOT NULL,
    name character varying(100),
    login character varying(50),
    pass character varying(50),
    role character varying(50)
);
    DROP TABLE public.members;
       public         heap    postgres    false    5            �            1259    33459    members_id_seq    SEQUENCE     �   CREATE SEQUENCE public.members_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 %   DROP SEQUENCE public.members_id_seq;
       public          postgres    false    222    5            i           0    0    members_id_seq    SEQUENCE OWNED BY     A   ALTER SEQUENCE public.members_id_seq OWNED BY public.members.id;
          public          postgres    false    221            �            1259    33496    orders    TABLE     �   CREATE TABLE public.orders (
    id integer NOT NULL,
    request_id integer,
    resource_type character varying(100),
    resource_name character varying(255),
    cost numeric
);
    DROP TABLE public.orders;
       public         heap    postgres    false    5            �            1259    33495    orders_id_seq    SEQUENCE     �   CREATE SEQUENCE public.orders_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 $   DROP SEQUENCE public.orders_id_seq;
       public          postgres    false    5    225            j           0    0    orders_id_seq    SEQUENCE OWNED BY     ?   ALTER SEQUENCE public.orders_id_seq OWNED BY public.orders.id;
          public          postgres    false    224            �            1259    33569    records    TABLE     �   CREATE TABLE public.records (
    transaction_id integer NOT NULL,
    drug character varying(255),
    packaging_quantity integer,
    total numeric(10,2),
    transaction_datetime timestamp without time zone
);
    DROP TABLE public.records;
       public         heap    postgres    false    5            �            1259    33568    records_transaction_id_seq    SEQUENCE     �   CREATE SEQUENCE public.records_transaction_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 1   DROP SEQUENCE public.records_transaction_id_seq;
       public          postgres    false    5    233            k           0    0    records_transaction_id_seq    SEQUENCE OWNED BY     Y   ALTER SEQUENCE public.records_transaction_id_seq OWNED BY public.records.transaction_id;
          public          postgres    false    232            �            1259    33483    reports    TABLE     �   CREATE TABLE public.reports (
    request_id integer NOT NULL,
    repair_type character varying(100),
    "time" character varying(50),
    cost numeric,
    resources text,
    reason text,
    help text
);
    DROP TABLE public.reports;
       public         heap    postgres    false    5            �            1259    33449    request_processes    TABLE     �   CREATE TABLE public.request_processes (
    request_id integer NOT NULL,
    priority character varying(50),
    date_finish_plan date
);
 %   DROP TABLE public.request_processes;
       public         heap    postgres    false    5            �            1259    33439    request_regs    TABLE     �   CREATE TABLE public.request_regs (
    request_id integer NOT NULL,
    client_name character varying(100),
    client_phone character varying(20),
    date_start date
);
     DROP TABLE public.request_regs;
       public         heap    postgres    false    5            �            1259    33417    requests    TABLE     �   CREATE TABLE public.requests (
    id integer NOT NULL,
    equip_num character varying(255),
    equip_type character varying(255),
    problem_desc character varying(255),
    request_comments text,
    status character varying(50)
);
    DROP TABLE public.requests;
       public         heap    postgres    false    5            �            1259    33416    requests_id_seq    SEQUENCE     �   CREATE SEQUENCE public.requests_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 &   DROP SEQUENCE public.requests_id_seq;
       public          postgres    false    216    5            l           0    0    requests_id_seq    SEQUENCE OWNED BY     C   ALTER SEQUENCE public.requests_id_seq OWNED BY public.requests.id;
          public          postgres    false    215            �           2604    33429    analyzes id    DEFAULT     j   ALTER TABLE ONLY public.analyzes ALTER COLUMN id SET DEFAULT nextval('public.analyzes_id_seq'::regclass);
 :   ALTER TABLE public.analyzes ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    218    217    218            �           2604    33537    assignments id    DEFAULT     p   ALTER TABLE ONLY public.assignments ALTER COLUMN id SET DEFAULT nextval('public.assignments_id_seq'::regclass);
 =   ALTER TABLE public.assignments ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    226    227    227            �           2604    33556    drugs drug_id    DEFAULT     n   ALTER TABLE ONLY public.drugs ALTER COLUMN drug_id SET DEFAULT nextval('public.drugs_drug_id_seq'::regclass);
 <   ALTER TABLE public.drugs ALTER COLUMN drug_id DROP DEFAULT;
       public          postgres    false    228    229    229            �           2604    33565    employees employee_id    DEFAULT     ~   ALTER TABLE ONLY public.employees ALTER COLUMN employee_id SET DEFAULT nextval('public.employees_employee_id_seq'::regclass);
 D   ALTER TABLE public.employees ALTER COLUMN employee_id DROP DEFAULT;
       public          postgres    false    230    231    231            �           2604    33463 
   members id    DEFAULT     h   ALTER TABLE ONLY public.members ALTER COLUMN id SET DEFAULT nextval('public.members_id_seq'::regclass);
 9   ALTER TABLE public.members ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    222    221    222            �           2604    33499 	   orders id    DEFAULT     f   ALTER TABLE ONLY public.orders ALTER COLUMN id SET DEFAULT nextval('public.orders_id_seq'::regclass);
 8   ALTER TABLE public.orders ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    225    224    225            �           2604    33572    records transaction_id    DEFAULT     �   ALTER TABLE ONLY public.records ALTER COLUMN transaction_id SET DEFAULT nextval('public.records_transaction_id_seq'::regclass);
 E   ALTER TABLE public.records ALTER COLUMN transaction_id DROP DEFAULT;
       public          postgres    false    232    233    233            �           2604    33420    requests id    DEFAULT     j   ALTER TABLE ONLY public.requests ALTER COLUMN id SET DEFAULT nextval('public.requests_id_seq'::regclass);
 :   ALTER TABLE public.requests ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    215    216    216            M          0    33426    analyzes 
   TABLE DATA           b   COPY public.analyzes (id, request_id, processing_time, expenses, profit, exe_quality) FROM stdin;
    public          postgres    false    218   �\       V          0    33534    assignments 
   TABLE DATA           P   COPY public.assignments (id, id_request, member_id, is_responsible) FROM stdin;
    public          postgres    false    227   �\       X          0    33553    drugs 
   TABLE DATA           �   COPY public.drugs (drug_id, name, manufacturer, packaging_quantity, measure_unit, dosage_mg, in_price, out_price, in_stock) FROM stdin;
    public          postgres    false    229   �\       Z          0    33562 	   employees 
   TABLE DATA           Z   COPY public.employees (employee_id, last_name, first_name, patronymic, phone) FROM stdin;
    public          postgres    false    231   E^       Q          0    33460    members 
   TABLE DATA           >   COPY public.members (id, name, login, pass, role) FROM stdin;
    public          postgres    false    222   ?_       T          0    33496    orders 
   TABLE DATA           T   COPY public.orders (id, request_id, resource_type, resource_name, cost) FROM stdin;
    public          postgres    false    225   s`       \          0    33569    records 
   TABLE DATA           h   COPY public.records (transaction_id, drug, packaging_quantity, total, transaction_datetime) FROM stdin;
    public          postgres    false    233   �`       R          0    33483    reports 
   TABLE DATA           a   COPY public.reports (request_id, repair_type, "time", cost, resources, reason, help) FROM stdin;
    public          postgres    false    223   �`       O          0    33449    request_processes 
   TABLE DATA           S   COPY public.request_processes (request_id, priority, date_finish_plan) FROM stdin;
    public          postgres    false    220   �`       N          0    33439    request_regs 
   TABLE DATA           Y   COPY public.request_regs (request_id, client_name, client_phone, date_start) FROM stdin;
    public          postgres    false    219   ,a       K          0    33417    requests 
   TABLE DATA           e   COPY public.requests (id, equip_num, equip_type, problem_desc, request_comments, status) FROM stdin;
    public          postgres    false    216   �a       m           0    0    analyzes_id_seq    SEQUENCE SET     >   SELECT pg_catalog.setval('public.analyzes_id_seq', 1, false);
          public          postgres    false    217            n           0    0    assignments_id_seq    SEQUENCE SET     A   SELECT pg_catalog.setval('public.assignments_id_seq', 20, true);
          public          postgres    false    226            o           0    0    drugs_drug_id_seq    SEQUENCE SET     @   SELECT pg_catalog.setval('public.drugs_drug_id_seq', 10, true);
          public          postgres    false    228            p           0    0    employees_employee_id_seq    SEQUENCE SET     G   SELECT pg_catalog.setval('public.employees_employee_id_seq', 6, true);
          public          postgres    false    230            q           0    0    members_id_seq    SEQUENCE SET     =   SELECT pg_catalog.setval('public.members_id_seq', 10, true);
          public          postgres    false    221            r           0    0    orders_id_seq    SEQUENCE SET     <   SELECT pg_catalog.setval('public.orders_id_seq', 1, false);
          public          postgres    false    224            s           0    0    records_transaction_id_seq    SEQUENCE SET     I   SELECT pg_catalog.setval('public.records_transaction_id_seq', 1, false);
          public          postgres    false    232            t           0    0    requests_id_seq    SEQUENCE SET     =   SELECT pg_catalog.setval('public.requests_id_seq', 6, true);
          public          postgres    false    215            �           2606    33433    analyzes analyzes_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.analyzes
    ADD CONSTRAINT analyzes_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.analyzes DROP CONSTRAINT analyzes_pkey;
       public            postgres    false    218            �           2606    33539    assignments assignments_pkey 
   CONSTRAINT     Z   ALTER TABLE ONLY public.assignments
    ADD CONSTRAINT assignments_pkey PRIMARY KEY (id);
 F   ALTER TABLE ONLY public.assignments DROP CONSTRAINT assignments_pkey;
       public            postgres    false    227            �           2606    33560    drugs drugs_pkey 
   CONSTRAINT     S   ALTER TABLE ONLY public.drugs
    ADD CONSTRAINT drugs_pkey PRIMARY KEY (drug_id);
 :   ALTER TABLE ONLY public.drugs DROP CONSTRAINT drugs_pkey;
       public            postgres    false    229            �           2606    33567    employees employees_pkey 
   CONSTRAINT     _   ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_pkey PRIMARY KEY (employee_id);
 B   ALTER TABLE ONLY public.employees DROP CONSTRAINT employees_pkey;
       public            postgres    false    231            �           2606    33465    members members_pkey 
   CONSTRAINT     R   ALTER TABLE ONLY public.members
    ADD CONSTRAINT members_pkey PRIMARY KEY (id);
 >   ALTER TABLE ONLY public.members DROP CONSTRAINT members_pkey;
       public            postgres    false    222            �           2606    33503    orders orders_pkey 
   CONSTRAINT     P   ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);
 <   ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_pkey;
       public            postgres    false    225            �           2606    33574    records records_pkey 
   CONSTRAINT     ^   ALTER TABLE ONLY public.records
    ADD CONSTRAINT records_pkey PRIMARY KEY (transaction_id);
 >   ALTER TABLE ONLY public.records DROP CONSTRAINT records_pkey;
       public            postgres    false    233            �           2606    33489    reports reports_pkey 
   CONSTRAINT     Z   ALTER TABLE ONLY public.reports
    ADD CONSTRAINT reports_pkey PRIMARY KEY (request_id);
 >   ALTER TABLE ONLY public.reports DROP CONSTRAINT reports_pkey;
       public            postgres    false    223            �           2606    33453 (   request_processes request_processes_pkey 
   CONSTRAINT     n   ALTER TABLE ONLY public.request_processes
    ADD CONSTRAINT request_processes_pkey PRIMARY KEY (request_id);
 R   ALTER TABLE ONLY public.request_processes DROP CONSTRAINT request_processes_pkey;
       public            postgres    false    220            �           2606    33443    request_regs request_regs_pkey 
   CONSTRAINT     d   ALTER TABLE ONLY public.request_regs
    ADD CONSTRAINT request_regs_pkey PRIMARY KEY (request_id);
 H   ALTER TABLE ONLY public.request_regs DROP CONSTRAINT request_regs_pkey;
       public            postgres    false    219            �           2606    33424    requests requests_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.requests
    ADD CONSTRAINT requests_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.requests DROP CONSTRAINT requests_pkey;
       public            postgres    false    216            �           2606    33541 1   assignments unique_assignment_request_responsible 
   CONSTRAINT     �   ALTER TABLE ONLY public.assignments
    ADD CONSTRAINT unique_assignment_request_responsible UNIQUE (id_request, is_responsible);
 [   ALTER TABLE ONLY public.assignments DROP CONSTRAINT unique_assignment_request_responsible;
       public            postgres    false    227    227            �           2606    33434 !   analyzes analyzes_request_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.analyzes
    ADD CONSTRAINT analyzes_request_id_fkey FOREIGN KEY (request_id) REFERENCES public.requests(id);
 K   ALTER TABLE ONLY public.analyzes DROP CONSTRAINT analyzes_request_id_fkey;
       public          postgres    false    218    3230    216            �           2606    33547 #   assignments fk_assignment_member_id    FK CONSTRAINT     �   ALTER TABLE ONLY public.assignments
    ADD CONSTRAINT fk_assignment_member_id FOREIGN KEY (member_id) REFERENCES public.members(id);
 M   ALTER TABLE ONLY public.assignments DROP CONSTRAINT fk_assignment_member_id;
       public          postgres    false    227    222    3238            �           2606    33542 $   assignments fk_assignment_request_id    FK CONSTRAINT     �   ALTER TABLE ONLY public.assignments
    ADD CONSTRAINT fk_assignment_request_id FOREIGN KEY (id_request) REFERENCES public.request_processes(request_id);
 N   ALTER TABLE ONLY public.assignments DROP CONSTRAINT fk_assignment_request_id;
       public          postgres    false    3236    227    220            �           2606    33504    orders orders_request_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_request_id_fkey FOREIGN KEY (request_id) REFERENCES public.request_processes(request_id);
 G   ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_request_id_fkey;
       public          postgres    false    3236    225    220            �           2606    33490    reports reports_request_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.reports
    ADD CONSTRAINT reports_request_id_fkey FOREIGN KEY (request_id) REFERENCES public.request_processes(request_id);
 I   ALTER TABLE ONLY public.reports DROP CONSTRAINT reports_request_id_fkey;
       public          postgres    false    223    3236    220            �           2606    33454 3   request_processes request_processes_request_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.request_processes
    ADD CONSTRAINT request_processes_request_id_fkey FOREIGN KEY (request_id) REFERENCES public.requests(id);
 ]   ALTER TABLE ONLY public.request_processes DROP CONSTRAINT request_processes_request_id_fkey;
       public          postgres    false    220    216    3230            �           2606    33444 )   request_regs request_regs_request_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.request_regs
    ADD CONSTRAINT request_regs_request_id_fkey FOREIGN KEY (request_id) REFERENCES public.requests(id);
 S   ALTER TABLE ONLY public.request_regs DROP CONSTRAINT request_regs_request_id_fkey;
       public          postgres    false    219    3230    216            M      x������ � �      V   -   x�3�4��,�2�&�i\F�F@�����0	 � �=... �U�      X   P  x��RQJ1�~9EOP6IW�w�0n�⇠��hQ���X�5{�ɍ���V��,��f�̼��g��)��8�;4������o��BR�Bʢ[C-e*�7Np�E<C��ϱƇ��X�K>�xV��T�/�r���c�"7J�`�nc��=�����oF��h�<`K'h���4�ޚZ:lxQ*e�RLI)�$��~I�K�ؽGa.�}��9ҡ!�]�L�_*$�9�9+ͱ�M㈓�u���\�ﶓ͹�T�'B�iIs���k���j��V��b�)fB���:��צ\�φYDte��lTԣ�Ⴞ�9�I�؜�1��rY�      Z   �   x�UPKj�0\KW)�k�.=Lb(��EY$��p"p�&gݨ#�-!�{�f�(�z\qC/�ń�1�y_���K�\Ð��S�*m��u�J-���;A�S����[�g�"O�w��mj���<L���gFb��7�jEE+isǀ3n�/J}bDH]!E
�YW�����ۦ��t{v�R�¥��#?=ۧea�c��5�m�VVz�FV'vƻ�=�P8�?���!/���/�R�_���I      Q   $  x�]�]J�@��'��
���-Ɨ�h�0ѷZAQQ�;1�ik��3;�ޙI��m&s�9�G�P��U�]���h�H�e&ÑHe&�JGC���ۙ��x����U�{�#�o4��	Se�g�j�a���s�u��'!�L��i��,�::���_Zq���F�n�������L@�L�Bߺ�r�h�9aޛ[�xs�gA�/pj��6�T��W4��i�(;�(/�n�[c��u��~��{2j���[�4�}���J��a�y￲�'J޲OLÜ5Ex��-�:9p�8����x:�      T      x������ � �      \      x������ � �      R      x������ � �      O   R   x�Uʩ�@@��^����Ux 9����@��iS�Pp`�N�梭h�F�Q�ǅgdܑ���	��%Ɵ��qW1��*X      N   �   x�U�M�0�u{��)?-w�0�1n\7nL�7@�Д3���,�d�b&�ojn\aD�4$�G����e�rI����v��� �YKy�Ll�(N#ct�p��x�޲��;s5ᅎ+.gJ�0�CY��R�����#�)��B��[j��%R*ч����Rt2      K   �   x�m�AN�@E�ݧ�MT ^�9��L&�q�� NK;�~��_V�^ѿ>����p_�eQo�8 a���ڢ�W��Q��U[�ԤO�q�V�����ƂU��f2�/�|���C
5�O�-Lɼ��Ic`��]�x��Z;#��ľ�c����m&�z�mt/��8�Y0m�d�T�7���Z�r���Ņ����5۪��;|�ԘJ�h?B�l���$��`I�9���t�w��e������     