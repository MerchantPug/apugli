var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI')
var Opcodes = Java.type('org.objectweb.asm.Opcodes')
var InsnList = Java.type('org.objectweb.asm.tree.InsnList')
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode')
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode')
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode')
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode')
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode')

function initializeCoreMod() {
    return {
        'apugli_edible_item': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraftforge.common.extensions.IForgeItemStack',
                'methodName': 'getFoodProperties',
                'methodDesc': '(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/food/FoodProperties;'
            },
            'transformer': function(node) {
                // if (CoreUtil.getEdibleItemPowerFoodProperties(entity, this.self())) { return CoreUtil.getEdibleItemPowerFoodProperties(entity, this.self()) }
                var ls = new InsnList();
                // this.self()
                ls.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
                ls.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraftforge/common/extensions/IForgeItemStack", "self", "()Lnet/minecraft/world/item/ItemStack;", true));
                // entity
                ls.add(new VarInsnNode(Opcodes.ALOAD, 1));
                ls.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/merchantpug/apugli/util/CoreUtil", "doEdibleItemPowersApply", "(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Z"));
                var label = new LabelNode();
				ls.add(new JumpInsnNode(Opcodes.IFEQ, label));
                // this.self()
                ls.add(new VarInsnNode(Opcodes.ALOAD, 0));
                ls.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraftforge/common/extensions/IForgeItemStack", "self", "()Lnet/minecraft/world/item/ItemStack;", true));
                // entity
                ls.add(new VarInsnNode(Opcodes.ALOAD, 1));
                ls.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/merchantpug/apugli/util/CoreUtil", "getEdibleItemPowerFoodProperties", "(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/food/FoodProperties;"));
				ls.add(new InsnNode(Opcodes.ARETURN));
				ls.add(label);
				node.instructions.insert(ls);
                return node;
            }
        }
    }
}